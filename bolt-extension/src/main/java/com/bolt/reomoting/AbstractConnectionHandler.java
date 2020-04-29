package com.bolt.reomoting;

import com.bolt.common.Constants;
import com.bolt.common.command.RemotingCommand;
import com.bolt.common.command.RequestCommand;
import com.bolt.common.command.ResponseCommand;
import com.bolt.common.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/24
 * @Description: TODO
 */
public abstract class AbstractConnectionHandler implements ConnectionHandler {
    private static final Logger logger = LoggerFactory.getLogger(AbstractConnectionHandler.class);

    protected ResponseFuture send(Connection connection, RequestCommand request) {
        Integer timeout = connection.getUrl().getParameter(Constants.TIMEOUT_KEY, Constants.DEFAULT_TIMEOUT);
        DefaultFuture future = DefaultFuture.newFuture(connection, request, timeout);
        connection.writeAndFlush(request).addListener(f -> {
            if (logger.isDebugEnabled()) {
                logger.debug("write to send message " + request.toString() +
                        "to" + connection.getRemoteAddress());
            }
            if (f.isSuccess()) {
                future.sent();
            }
            if (!f.isSuccess()) {
                // 取消
                future.cancel();
                throw new RemotingException(connection, createErrorMsg(connection, request, f.cause()));
            }
        });

        return future;
    }

    public void send(Connection connection, ResponseCommand response) {
        connection.getChannel().writeAndFlush(response).addListener(f -> {
            if (logger.isDebugEnabled()) {
                logger.debug("write to send message" + response.toString() +
                        "to" + connection.getRemoteAddress());
            }
            if (!f.isSuccess()) {
                throw new RemotingException(connection, createErrorMsg(connection, response, f.cause()));
            }
        });
    }

    private String createErrorMsg(Connection connection, RemotingCommand cmd, Throwable t) {
        StringBuffer errorMsg = new StringBuffer();
        errorMsg.append("Failed to send message " + cmd.toString() + " to "
                + connection.getRemoteAddress() + ", request id " + cmd.getId());
        if (t != null) {
            errorMsg.append(", error message is:" + t.getMessage());
        }
        return errorMsg.toString();
    }
}
