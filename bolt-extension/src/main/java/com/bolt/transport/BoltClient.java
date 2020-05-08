package com.bolt.transport;

import com.bolt.codec.BoltCodec;
import com.bolt.codec.Codec;
import com.bolt.codec.CodecAdapter;
import com.bolt.common.Constants;
import com.bolt.common.Url;
import com.bolt.common.exception.RemotingException;
import com.bolt.config.BoltClientOption;
import com.bolt.config.BoltGenericOption;
import com.bolt.config.BoltRemotingOption;
import com.bolt.protocol.BoltProtocol;
import com.bolt.protocol.Protocol;
import com.bolt.reomoting.Connection;
import com.bolt.util.NetUtils;
import com.bolt.util.UrlUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.ChannelHealthChecker;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/9
 * @Description: TODO
 */
public class BoltClient extends AbstractClient<InetSocketAddress, FixedChannelPool> {
    private static final Logger logger = LoggerFactory.getLogger(BoltClient.class);
    private static final NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(Constants.DEFAULT_IO_THREADS, new DefaultThreadFactory("NettyClientWorker", true));
    private Bootstrap bootstrap;

    public BoltClient() {
        this(new BoltCodec(), new BoltProtocol());
    }

    public BoltClient(Codec codec, Protocol protocol) {
        super(codec, protocol);
    }

    @Override
    protected FixedChannelPool newPool(InetSocketAddress key) {
        Url url = getUrl();
        Long acquireTimeout = url.getParameter(Constants.ACQUIRE_TIMEOUT,
                this.option(BoltClientOption.ACQUIRE_TIMEOUT));
        Integer maxConnection = url.getParameter(Constants.MAX_CONNECTION,
                this.option(BoltClientOption.MAX_CONNECTION));
        Integer maxPendingAcquires = url.getParameter(Constants.MAX_PENDING_ACQUIRES,
                this.option(BoltClientOption.MAX_PENDING_ACQUIRES));
        Boolean relaseHealthCheck = url.getParameter(Constants.RELEASE_HEALTH_CHECK,
                this.option(BoltClientOption.RELEASE_HEALTH_CHECK));
        Boolean lastRecentUsed = url.getParameter(Constants.CONNECTION_LAST_RECENT_USED,
                this.option(BoltClientOption.CONNECTION_LAST_RECENT_USED));
        String action = url.getParameter(Constants.ACQUIRE_TIMEOUT_ACTION,
                this.option(BoltClientOption.ACQUIRE_TIMEOUT_ACTION));
        FixedChannelPool.AcquireTimeoutAction timeoutAction = "new".equals(action)
                ? FixedChannelPool.AcquireTimeoutAction.NEW : FixedChannelPool.AcquireTimeoutAction.FAIL;
        BoltHandler clientHandler = new BoltHandler(getUrl(), getProtocol(), isServerSide());
        clientHandler.setConnectionEventListener(getConnectionEventListener());
        clientHandler.setReconnectClient(reconnectClient);
        int heartbeatInterval = UrlUtils.getHeartbeat(url);

        return new FixedChannelPool(bootstrap.remoteAddress(key), new ChannelPoolHandler() {
            @Override
            public void channelReleased(io.netty.channel.Channel ch) throws Exception {
                if (logger.isDebugEnabled()) {
                    logger.debug("The channel {} has release ChannelPool, time {}", ch, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
                }
            }

            @Override
            public void channelAcquired(io.netty.channel.Channel ch) throws Exception {
                if (logger.isDebugEnabled()) {
                    logger.debug("The channel {} has acquire ChannelPool, time {}", ch, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
                }
            }

            @Override
            public void channelCreated(io.netty.channel.Channel ch) throws Exception {
                CodecAdapter adapter = new CodecAdapter(getCodec(), getUrl());
                ch.pipeline()
                        .addLast("decoder", adapter.getDecoder())
                        .addLast("encoder", adapter.getEncoder())
                        .addLast("client-idle-handler", new IdleStateHandler(heartbeatInterval, 0, 0, MILLISECONDS))
                        .addLast(clientHandler);
            }
        }, ChannelHealthChecker.ACTIVE, timeoutAction, acquireTimeout,
                maxConnection, maxPendingAcquires, relaseHealthCheck, lastRecentUsed);
    }

    @Override
    protected void doOpen() throws Throwable {
        this.bootstrap = new Bootstrap();
        bootstrap.group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, this.option(BoltGenericOption.TCP_NODELAY))
                .option(ChannelOption.SO_REUSEADDR, this.option(BoltGenericOption.TCP_SO_REUSEADDR))
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.WRITE_BUFFER_WATER_MARK, initWriteBufferWaterMark());
    }


    @Override
    protected void doClose() throws Throwable {
        nioEventLoopGroup.shutdownGracefully();

    }

    @Override
    public Connection ctreateConnectionIfAbsent(Url url) throws RemotingException {
        Channel ch = null;
        int connectTimeout = getConnectTimeout();
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout);
        InetSocketAddress connectAddresss = getConnectAddresss();
        FixedChannelPool channelPool = get(connectAddresss);
        try {
            Long start = System.currentTimeMillis();
            Future<io.netty.channel.Channel> future = channelPool.acquire();
            boolean ret = future.awaitUninterruptibly(connectTimeout, TimeUnit.MILLISECONDS);
            if (ret && future.isSuccess()) {
                ch = future.getNow();
            } else if (future.cause() != null) {
                String error = "client(url: " + url + ") failed to connect to server or from channelPool "
                        + connectAddresss + ", error message is:" + future.cause().getMessage();
                throw new RemotingException(null, connectAddresss, error, future.cause());
            } else {
                String error = "client(url: " + url + ") failed to connect to server or from channelPool "
                        + connectAddresss + " client-side timeout "
                        + connectTimeout + "ms (elapsed: " + (System.currentTimeMillis() - start) + "ms) from netty client "
                        + NetUtils.getLocalHost();
                throw new RemotingException(null, connectAddresss, error, future.cause());
            }
        } finally {
            if (ch != null && ch.isActive()) {
                Future<Void> releaseFuture = channelPool.release(ch);
                releaseFuture.addListener(f -> {
                    if (f.cause() != null) {
                        String error = "client(url: " + url + ") failed to release to channelPool "
                                + connectAddresss + ", error message is:" + f.cause().getMessage();
                        logger.error(error);
                    }
                });
            }
        }
        return Connection.getOrAddConnection(ch, url);
    }

}
