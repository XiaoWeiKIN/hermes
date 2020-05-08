package com.bolt.transport;

import com.bolt.codec.BoltCodec;
import com.bolt.codec.CodecAdapter;
import com.bolt.common.Url;
import com.bolt.config.BoltGenericOption;
import com.bolt.config.BoltRemotingOption;
import com.bolt.config.BoltServerOption;
import com.bolt.reomoting.Connection;
import com.bolt.protocol.BoltProtocol;
import com.bolt.util.NetUtils;
import com.bolt.util.UrlUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Map;

import static java.util.concurrent.TimeUnit.MILLISECONDS;


/**
 * @Author: wangxw
 * @DateTime: 2020/4/17
 * @Description: TODO
 */
public class BoltServer extends AbstractServer<BoltServer> {
    private static final Logger logger = LoggerFactory.getLogger(BoltServer.class);
    private ServerBootstrap bootstrap;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private volatile Channel channel;

    public BoltServer() {
        super(new BoltCodec(), new BoltProtocol());
    }


    @Override
    public void doOpen() throws Throwable {
        bootstrap = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("BoltServerBoss", false));
        workerGroup = new NioEventLoopGroup(this.option(BoltGenericOption.IO_THREADS), new DefaultThreadFactory("BoltServerWorker", true));
        port = this.option(BoltServerOption.PORT);
        init(new Url(NetUtils.getLocalHost(), port));
        final BoltHandler serverHandler = new BoltHandler(getUrl(), getProtocol(), isServerSide());
        int idleTimeout = UrlUtils.getIdleTimeout(getUrl());
        serverHandler.setConnectionEventListener(getConnectionEventListener());


        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, this.option(BoltGenericOption.TCP_NODELAY))
                .childOption(ChannelOption.SO_REUSEADDR, this.option(BoltGenericOption.TCP_SO_REUSEADDR))
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, initWriteBufferWaterMark())
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        CodecAdapter adapter = new CodecAdapter(getCodec(), getUrl());
                        ch.pipeline()
                                .addLast("decoder", adapter.getDecoder())
                                .addLast("encoder", adapter.getEncoder())
                                .addLast("server-idle-handler", new IdleStateHandler(0, 0, idleTimeout, MILLISECONDS))
                                .addLast(serverHandler);
                    }
                });
        ChannelFuture future = bootstrap.bind(this.port).syncUninterruptibly();
        channel = future.channel();
    }

    @Override
    protected void doClose() throws Throwable {
        try {
            if (channel != null) {
                // unbind.
                channel.close();
            }
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }

        try {
            if (bootstrap != null) {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
    }
}
