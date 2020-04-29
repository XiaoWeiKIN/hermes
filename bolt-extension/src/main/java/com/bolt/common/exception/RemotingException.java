package com.bolt.common.exception;

import com.bolt.reomoting.Connection;

import java.net.InetSocketAddress;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/20
 * @Description: TODO
 */
public class RemotingException extends RuntimeException {

    private static final long serialVersionUID = -6507663755035631766L;
    private InetSocketAddress localAddress;

    private InetSocketAddress remoteAddress;
    public RemotingException(Connection connection, String msg) {
        this(connection == null ? null : connection.getLocalAddress(),
                connection == null ? null : connection.getRemoteAddress(), msg);
    }

    public RemotingException(InetSocketAddress localAddress, InetSocketAddress remoteAddress, String message) {
        super(message);

        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
    }

    public RemotingException(Connection connection, Throwable cause) {
        this(connection == null ? null : connection.getLocalAddress(),
                connection == null ? null : connection.getRemoteAddress(), cause);
    }

    public RemotingException(InetSocketAddress localAddress, InetSocketAddress remoteAddress, Throwable cause) {
        super(cause);
        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
    }

    public RemotingException(Connection connection, String message, Throwable cause) {
        this(connection == null ? null : connection.getLocalAddress(),
                connection == null ? null : connection.getRemoteAddress(), message, cause);
    }

    public RemotingException(InetSocketAddress localAddress, InetSocketAddress remoteAddress, String message,
                             Throwable cause) {
        super(message, cause);
        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
    }

    public InetSocketAddress getLocalAddress() {
        return localAddress;
    }

    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }
}
