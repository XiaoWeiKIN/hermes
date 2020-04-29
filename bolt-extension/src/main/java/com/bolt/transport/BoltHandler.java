package com.bolt.transport;

import com.bolt.common.Url;
import com.bolt.common.command.RequestCommand;
import com.bolt.common.command.ResponseCommand;
import com.bolt.common.enums.ResponseStatus;
import com.bolt.common.exception.RemotingException;
import com.bolt.reomoting.Connection;
import com.bolt.reomoting.RemotingContext;
import com.bolt.common.command.RemotingCommand;
import com.bolt.protocol.CommandHandler;
import com.bolt.protocol.Protocol;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.RejectedExecutionException;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/19
 * @Description: TODO
 */
@ChannelHandler.Sharable
public class BoltHandler extends ChannelInboundHandlerAdapter {
    private final Protocol protocol;

    private Url url;

    public BoltHandler(Url url, Protocol protocol) {
        this.url = url;
        this.protocol = protocol;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Connection connection = Connection.getOrAddConnection(ctx.channel(), url);
        RemotingCommand command = (RemotingCommand) msg;
        try {
            CommandHandler handler = protocol.getCommandHandler(command.getCmdCode());
            handler.handle(new RemotingContext(ctx, ctx.channel().eventLoop(),
                    protocol.getDefaultExecutor(), connection), command);
        } catch (Throwable t) {
            // 要处理使用业务线程池被打满没有返回值，导致客户端超时的情况
            if (command instanceof RequestCommand && t instanceof RejectedExecutionException) {
                String errorMsg = "Server side(" + url.getHost() + "," + url.getPort() + ") threadpool is exhausted ,detail msg:" + t.getMessage();
                RequestCommand request = (RequestCommand) command;
                if (request.isTwoWay()) {
                    ResponseCommand respnse = new ResponseCommand(request.getId(), request.getCmdCode());
                    respnse.setStatus(ResponseStatus.SERVER_THREADPOOL_BUSY);
                    respnse.setErrorMessage(errorMsg);
                    connection.send(respnse);
                    return;
                }
            }
            throw new RemotingException(connection, t.getCause());
        } finally {
            Connection.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.out.println(cause);
    }
}
