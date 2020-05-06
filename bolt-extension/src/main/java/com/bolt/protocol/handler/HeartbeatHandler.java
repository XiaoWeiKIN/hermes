package com.bolt.protocol.handler;

import com.bolt.common.Constants;
import com.bolt.common.Invocation;
import com.bolt.common.Url;
import com.bolt.common.Version;
import com.bolt.common.command.CommandCode;
import com.bolt.common.command.RequestCommand;
import com.bolt.common.command.ResponseCommand;
import com.bolt.common.enums.CommandCodeEnum;
import com.bolt.common.enums.ResponseStatus;
import com.bolt.common.exception.RemotingException;
import com.bolt.reomoting.Connection;
import com.bolt.reomoting.DefaultFuture;
import com.bolt.reomoting.RemotingContext;
import com.bolt.reomoting.ResponseCallback;
import com.bolt.transport.ReconnectManager;
import io.netty.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: wangxw
 * @DateTime: 2020/5/4
 * @Description: TODO
 */
public class HeartbeatHandler extends AbstractCommandHandler {
    private static final Logger logger = LoggerFactory.getLogger(HeartbeatHandler.class);
    private Lock connectionLock = new ReentrantLock();
    private ReconnectManager reconnectManager;

    @Override
    public Object handleRequest(RemotingContext ctx, RequestCommand request) throws Exception {
        ResponseCommand response = new ResponseCommand(request.getId(), request.getCmdCode());
        response.setStatus(ResponseStatus.SUCCESS);
        response.setHeartbeat(true);
        response.setVersion(Version.getProtocolVersion());
        Thread.sleep(3000);
        if (logger.isDebugEnabled()) {
            logger.debug("Received heartbeat from remote connection " + ctx.getConnection());
        }
        return response;
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCodeEnum.HEARTBEAT_CMD;
    }

    public void sendHeartbeat(Connection connection) {
        // 心跳次数
        Integer heartbeatCount = connection.attr(Connection.HEARTBEAT_COUNT).get();
        Integer maxCount = connection.getUrl().getParameter(Constants.MAX_HEARTBEAT_COUNT,
                Constants.DEFAULT_MAX_HEARTBEAT_COUNT);
        if (heartbeatCount > maxCount) {
            // 关闭连接并重连
            connectionLock.lock();
            try {
                if (connection.isActive()) {
                    connection.close();
                }
                if(!connection.isActive()){
                    reconnectManager.reconnect(connection.getUrl());
                }
            } finally {
                connectionLock.unlock();
            }

        } else {
            RequestCommand request = new RequestCommand(CommandCodeEnum.HEARTBEAT_CMD);
            request.setTwoWay(true);
            request.setVersion(Version.getProtocolVersion());
            request.setHeartbeat(true);

            connection.send(request, 1000).whenComplete(((res, cause) -> {
                if (cause != null) {
                    Integer oldCount;
                    Integer newCount;
                    Attribute<Integer> HEARTBEAT_COUNT = connection.attr(Connection.HEARTBEAT_COUNT);
                    do {
                        oldCount = HEARTBEAT_COUNT.get();
                        newCount = oldCount + 1;
                    } while (!HEARTBEAT_COUNT.compareAndSet(oldCount, newCount));
                    return;
                }
            }));
        }
    }

    public void setReconnectManager(ReconnectManager reconnectManager) {
        this.reconnectManager = reconnectManager;
    }
}
