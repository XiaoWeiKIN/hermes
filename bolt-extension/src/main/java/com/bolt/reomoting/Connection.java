package com.bolt.reomoting;


import com.bolt.common.Url;
import com.bolt.common.command.RequestCommand;
import com.bolt.common.command.ResponseCommand;
import com.bolt.common.exception.RemotingException;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/9
 * @Description: TODO
 */
public final class Connection extends AbstractConnectionHandler {
    private static final Logger logger = LoggerFactory.getLogger(Connection.class);
    public static final ConcurrentMap<io.netty.channel.Channel, Connection> connectionMap = new ConcurrentHashMap<io.netty.channel.Channel, Connection>();
    private Channel channel;
    private Url url;

    public Connection(Channel channel) {
        this(channel, null);
    }

    public Connection(Channel channel, Url url) {
        this.channel = channel;
        this.url = url;
    }

    public Channel getChannel() {
        return this.channel;
    }

    public Url getUrl() {
        return this.url;
    }

    public boolean isWritable() {
        return this.channel.isWritable();
    }


    public ChannelFuture writeAndFlush(Object msg) throws RemotingException {
        if (channel == null) {
            throw new RemotingException(this, "The connection has no valid channel, Connection url" + this.url);
        }
        if (!isWritable()) {
            logger.error("The connection {} write overflow !!!", this);
            throw new RemotingException(this, "The connection {} write overflow !!!" + this);
        }
        return channel.writeAndFlush(msg);
    }
    @Override
    public ResponseFuture send(RequestCommand request) {
        return super.send(this, request);
    }
    @Override
    public void send(ResponseCommand response) {
        super.send(this, response);
    }

    public void close() {
        try {
            if (logger.isInfoEnabled()) {
                logger.info("Close netty channel " + channel);
            }
            try {
                channel.close();
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }

            try {
                removeChannelIfDisconnected(channel);
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

    public InetSocketAddress getLocalAddress() {
        if (channel == null) {
            return null;
        }
        return (InetSocketAddress) this.channel.localAddress();
    }

    public InetSocketAddress getRemoteAddress() {
        if (channel == null) {
            return null;
        }
        return (InetSocketAddress) this.channel.remoteAddress();
    }

    public static Connection getOrAddConnection(Channel ch, Url url) {
        if (ch == null) {
            return null;
        }
        Connection ret = connectionMap.get(ch);
        if (ret == null) {
            Connection boltChannel = new Connection(ch, url);
            if (ch.isActive()) {
                ret = connectionMap.putIfAbsent(ch, boltChannel);
            }
            if (ret == null) {
                ret = boltChannel;
            }
        }
        return ret;
    }

    public static Connection getConnection(Channel ch) {
        if (ch == null) {
            return null;
        }
        return connectionMap.get(ch);
    }

    public static void removeChannelIfDisconnected(io.netty.channel.Channel ch) {
        if (ch != null && !ch.isActive()) {
            connectionMap.remove(ch);
        }
    }

    public static void clear() {
        try {
            for (Map.Entry<io.netty.channel.Channel, Connection> entry : connectionMap.entrySet()) {
                io.netty.channel.Channel ch = entry.getKey();
                removeChannelIfDisconnected(ch);
            }
        } catch (Throwable t) {
            logger.warn(t.getMessage(), t);
        }


    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((channel == null) ? 0 : channel.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Connection other = (Connection) obj;
        if (channel == null) {
            if (other.channel != null) {
                return false;
            }
        } else if (!channel.equals(other.channel)) {
            return false;
        }
        return true;
    }


}
