package com.bolt.reomoting;

import com.bolt.common.Constants;
import com.bolt.common.Invocation;
import com.bolt.common.command.RequestCommand;
import com.bolt.common.command.ResponseCommand;
import com.bolt.common.enums.CommandCodeEnum;
import com.bolt.common.enums.ResponseStatus;
import com.bolt.common.exception.RemotingException;
import com.bolt.common.exception.TimeoutException;
import com.bolt.util.NamedThreadFactory;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/24
 * @Description: TODO
 */
public class DefaultFuture implements ResponseFuture {

    private static final Logger logger = LoggerFactory.getLogger(DefaultFuture.class);

    private static final ConcurrentMap<Integer, Connection> CONNECTIONS = new ConcurrentHashMap<>();

    private static final ConcurrentMap<Integer, DefaultFuture> FUTURES = new ConcurrentHashMap<>();

    private final Lock lock = new ReentrantLock();
    // invoke id.
    private final int id;
    private final Connection connection;
    private final RequestCommand request;
    private final int timeout;
    private final CountDownLatch downLatch = new CountDownLatch(1);
    private final long start = System.currentTimeMillis();
    private volatile long sent;
    private volatile ResponseCommand response;
    private Timeout timeoutCheckTask;
    private ResponseCallback callback;
    public static final Timer TIME_OUT_TIMER = new HashedWheelTimer(
            new NamedThreadFactory("bolt-future-timeout", true),
            30,
            TimeUnit.MILLISECONDS);

    public DefaultFuture(Connection connection, RequestCommand request, int timeout) {
        this.id = request.getId();
        this.connection = connection;
        this.request = request;
        this.timeout = timeout;
        // put into waiting map.
        FUTURES.put(id, this);
        CONNECTIONS.put(id, connection);
    }

    @Override
    public <T> T get() throws RemotingException {
        return get(timeout);
    }

    @Override
    public <T> T get(int timeout) throws RemotingException {
        if (timeout <= 0) {
            timeout = Constants.DEFAULT_TIMEOUT;
        }
        try {
            downLatch.await(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (!isDone()) {
            throw new TimeoutException(sent > 0, connection, getTimeoutMessage(false));
        }

        return returnFromResponse();
    }

    @Override
    public void setCallback(ResponseCallback callback) {
        if (isDone()) {
            invokeCallback(callback);
        } else {
            boolean isdone = false;
            lock.lock();
            try {
                if (!isDone()) {
                    this.callback = callback;
                } else {
                    isdone = true;
                }
            } finally {
                lock.unlock();
            }
            if (isdone) {
                invokeCallback(callback);
            }
        }
    }

    @Override
    public boolean isDone() {
        return response != null;
    }

    public void cancel() {
        ResponseCommand errorResult = new ResponseCommand(request.getCmdCode());
        errorResult.setStatus(ResponseStatus.CLIENT_SEND_ERROR);
        errorResult.setErrorMessage("request future has been canceled.");
        response = errorResult;
        FUTURES.remove(id);
        CONNECTIONS.remove(id);
    }

    /**
     * init a DefaultFuture
     * 1.init a DefaultFuture
     * 2.timeout check
     *
     * @param connection connection
     * @param request    the request
     * @param timeout    timeout
     * @return a new DefaultFuture
     */
    public static DefaultFuture newFuture(Connection connection, RequestCommand request, int timeout) {
        final DefaultFuture future = new DefaultFuture(connection, request, timeout);
        // timeout check
        timeoutCheck(future);
        return future;
    }

    public static void received(Connection connection, ResponseCommand response) {
        try {
            DefaultFuture future = FUTURES.remove(response.getId());
            if (future != null) {
                Timeout timeout = future.timeoutCheckTask;
                if (timeout != null) {
                    timeout.cancel();
                }
                // 重置心跳次数
                if(response.isHeartbeat()&&ResponseStatus.SUCCESS.equals(response.getStatus())){
                    connection.resetHeartbeat();
                }

                future.doReceived(response);
            } else {
                logger.warn("The timeout response finally returned at "
                        + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()))
                        + ", response " + response
                        + (connection == null ? "" : ", connection: " + connection.getLocalAddress()
                        + " -> " + connection.getRemoteAddress()));
            }
        } finally {
            CONNECTIONS.remove(response.getId());
        }
    }

    public void sent() {
        sent = System.currentTimeMillis();
    }


    private void doReceived(ResponseCommand res) {
        response = res;
        downLatch.countDown();

        if (callback != null) {
            invokeCallback(callback);
        }
    }

    private void invokeCallback(ResponseCallback c) {
        ResponseCallback callbackCopy = c;
        if (callbackCopy == null) {
            throw new NullPointerException("callback cannot be null.");
        }
        c = null;
        ResponseCommand res = response;
        if (res == null) {
            throw new IllegalStateException("response cannot be null. url:" + connection.getUrl());
        }

        if (ResponseStatus.SUCCESS.equals(res.getStatus())) {
            try {
                callbackCopy.done(response.getInvocation());
            } catch (Exception e) {
                logger.error("callback invoke error .inv:" + res.getInvocation() + ",url:" + connection.getUrl(), e);
            }
        } else if (res.getStatus() == ResponseStatus.CLIENT_TIMEOUT || res.getStatus() == ResponseStatus.SERVER_TIMEOUT) {
            try {
                TimeoutException te = new TimeoutException(res.getStatus() == ResponseStatus.SERVER_TIMEOUT, connection, res.getErrorMessage());
                callbackCopy.caught(te);
            } catch (Exception e) {
                logger.error("callback invoke error ,url:" + connection.getUrl(), e);
            }
        } else {
            try {
                RuntimeException re = new RuntimeException(res.getErrorMessage());
                callbackCopy.caught(re);
            } catch (Exception e) {
                logger.error("callback invoke error ,url:" + connection.getUrl(), e);
            }
        }

    }

    private <T> T returnFromResponse() throws RemotingException {
        ResponseCommand res = response;
        if (res == null) {
            throw new IllegalStateException("response cannot be null");
        }
        if (res.getStatus() == ResponseStatus.SUCCESS) {
            Invocation invocation = res.getInvocation();
            if (invocation == null) {
                throw new IllegalStateException("response data cannot be null");

            }
            return (T) invocation.getData();

        }
        if (res.getStatus() == ResponseStatus.CLIENT_TIMEOUT || res.getStatus() == ResponseStatus.SERVER_TIMEOUT) {
            throw new TimeoutException(res.getStatus() == ResponseStatus.SERVER_TIMEOUT, connection, res.getErrorMessage());
        }
        throw new RemotingException(connection, res.getErrorMessage());
    }

    private String getTimeoutMessage(boolean scan) {
        long nowTimestamp = System.currentTimeMillis();
        return (request.getCmdCode().equals(CommandCodeEnum.HEARTBEAT_CMD) ? "Heartbeat " : ("CommandCode " + request.getCmdCode()))
                + (sent > 0 ? " Waiting server-side response timeout" : " Sending request timeout in client-side")
                + (scan ? " by scan timer" : "") + ". start time: "
                + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(start))) + ", end time: "
                + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date())) + ","
                + (sent > 0 ? " client elapsed: " + (sent - start)
                + " ms, server elapsed: " + (nowTimestamp - sent)
                : " elapsed: " + (nowTimestamp - start)) + " ms, timeout: "
                + timeout + " ms, request: " + request + ", channel: " + connection.getLocalAddress()
                + " -> " + connection.getRemoteAddress();
    }

    /**
     * check time out of the future
     */
    private static void timeoutCheck(DefaultFuture future) {
        TimeoutCheckTask task = new TimeoutCheckTask(future);
        future.timeoutCheckTask = TIME_OUT_TIMER.newTimeout(task, future.getTimeout(), TimeUnit.MILLISECONDS);
    }

    private static class TimeoutCheckTask implements TimerTask {

        private DefaultFuture future;

        TimeoutCheckTask(DefaultFuture future) {
            this.future = future;
        }

        @Override
        public void run(Timeout timeout) {
            if (future == null || future.isDone()) {
                return;
            }
            // create exception response.
            ResponseCommand timeoutResponse = new ResponseCommand(future.getId(), future.getRequest().getCmdCode());
            // set timeout status.
            timeoutResponse.setStatus(future.isSent() ? ResponseStatus.SERVER_TIMEOUT : ResponseStatus.CLIENT_TIMEOUT);
            timeoutResponse.setErrorMessage(future.getTimeoutMessage(true));
            // handle response.
            DefaultFuture.received(future.getConnection(), timeoutResponse);

        }
    }

    private int getId() {
        return id;
    }

    public RequestCommand getRequest() {
        return request;
    }

    private boolean isSent() {
        return sent > 0;
    }

    public Connection getConnection() {
        return connection;
    }

    public int getTimeout() {
        return timeout;
    }

}
