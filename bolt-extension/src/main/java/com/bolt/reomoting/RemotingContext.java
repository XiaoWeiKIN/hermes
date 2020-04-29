package com.bolt.reomoting;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.FastThreadLocal;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/20
 * @Description: TODO
 */
public class RemotingContext {
    private static final FastThreadLocal<RemotingContext> LOCAL = new FastThreadLocal<RemotingContext>() {
        @Override
        protected RemotingContext initialValue() {
            return new RemotingContext();
        }
    };
    private Future<?> future;

    private ChannelHandlerContext context;
    /**
     * 协议级别的线程池
     */
    private ExecutorService protocolExecutor;
    /**
     * IO线程
     */
    private EventLoop eventLoop;

    private Connection connection;

    public static RemotingContext getContext(){
        return LOCAL.get();
    }

    public static void removeContext() {
        LOCAL.remove();
    }

    protected RemotingContext() {
    }

    public RemotingContext(ChannelHandlerContext context, EventLoop eventLoop, ExecutorService protocolExecutor,Connection connection) {
        this.context = context;
        this.eventLoop = eventLoop;
        this.protocolExecutor = protocolExecutor;
        this.connection=connection;
    }


    public ChannelFuture writeAndFlush(Object msg) {
        return this.context.writeAndFlush(msg);
    }

    public ExecutorService protocolExecutor() {
        return protocolExecutor;
    }

    public EventLoop getEventLoop() {
        return eventLoop;
    }

    public Connection getConnection() {
        return connection;
    }

    public<T> CompletableFuture<T>getCompletableFuture() {
        return (CompletableFuture<T>) future;
    }

    public void setFuture(Future<?> future) {
        this.future = future;
    }
}
