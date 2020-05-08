package com.bolt.transport;

import com.bolt.common.Url;
import com.bolt.common.command.RequestCommand;
import com.bolt.common.command.ResponseCommand;
import com.bolt.common.enums.CommandCodeEnum;
import com.bolt.common.enums.ConnectionEventType;
import com.bolt.common.enums.ResponseStatus;
import com.bolt.common.exception.ExecutionException;
import com.bolt.protocol.handler.HeartbeatHandler;
import com.bolt.reomoting.Connection;
import com.bolt.reomoting.ConnectionEventListener;
import com.bolt.reomoting.RemotingContext;
import com.bolt.common.command.RemotingCommand;
import com.bolt.protocol.handler.CommandHandler;
import com.bolt.protocol.Protocol;
import com.bolt.util.ObjectUtils;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionException;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/19
 * @Description: TODO
 */
@ChannelHandler.Sharable
public class BoltHandler extends ChannelDuplexHandler {
    private static final Logger logger = LoggerFactory.getLogger(BoltHandler.class);
    private final Protocol protocol;
    private Url url;
    private final boolean serverSide;

    private ConnectionEventListener eventListener;

    private ReconnectClient reconnectClient;

    public BoltHandler(Url url, Protocol protocol, boolean serverSide) {
        this.url = url;
        this.protocol = protocol;
        this.serverSide = serverSide;
    }

    public void setConnectionEventListener(ConnectionEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void setReconnectClient(ReconnectClient reconnectClient) {
        this.reconnectClient = reconnectClient;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Connection connection = Connection.getOrAddConnection(ctx.channel(), url);
        protocol.getDefaultExecutor().execute(() -> {
            eventListener.onEvent(ConnectionEventType.CONNECT, connection);
        });

        if (logger.isInfoEnabled()) {
            logger.info("The connection of " + connection.getLocalAddress() + " -> " + connection.getRemoteAddress() + " is established.");
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Connection connection = Connection.getOrAddConnection(ctx.channel(), url);
        try {
            protocol.getDefaultExecutor().execute(() -> {
                eventListener.onEvent(ConnectionEventType.CLOSE, connection);
            });

            if (logger.isInfoEnabled()) {
                logger.info("The connection of " + connection.getLocalAddress() + " -> " + connection.getRemoteAddress() + " is disconnected.");
            }
        } finally {
            Connection.removeChannelIfDisconnected(ctx.channel());
        }
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
                ResponseCommand response = new ResponseCommand(request.getId(), request.getCmdCode());
                response.setStatus(ResponseStatus.SERVER_THREADPOOL_BUSY);
                response.setErrorMessage(errorMsg);
                connection.sendResponseIfNecessary(request, response);
                return;
            }
            throw new ExecutionException(command, connection, t.getMessage());
        } finally {
            Connection.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Connection connection = Connection.getOrAddConnection(ctx.channel(), url);
        try {
            if (cause instanceof ExecutionException) {
                ExecutionException e = (ExecutionException) cause;
                RemotingCommand command = e.getCommand();
                if (command instanceof RequestCommand) {
                    RequestCommand request = (RequestCommand) command;
                    ResponseCommand response = new ResponseCommand(request.getId(), request.getCmdCode());
                    response.setStatus(ResponseStatus.SERVER_EXCEPTION);
                    response.setErrorMessage(ObjectUtils.toString(e));
                    connection.sendResponseIfNecessary(request, response);
                }
            }
            logger.warn(ObjectUtils.toString(cause));
        } finally {
            Connection.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            Connection connection = Connection.getOrAddConnection(ctx.channel(), url);
            try {
                if (serverSide) {
                    // 直接关闭连接,等待下次调用时重新建立连接
                    connection.close();
                } else {

                    HeartbeatHandler handler = (HeartbeatHandler) protocol.getCommandHandler(CommandCodeEnum.HEARTBEAT_CMD);
                    handler.setReconnectClient(reconnectClient);
                    handler.sendHeartbeat(connection);
                }
            } finally {
                Connection.removeChannelIfDisconnected(ctx.channel());
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}
