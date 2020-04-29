package com.bolt.transport;

import com.bolt.codec.Codec;
import com.bolt.common.exception.RemotingException;
import com.bolt.protocol.Protocol;
import com.bolt.util.ExecutorUtil;
import com.bolt.util.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/21
 * @Description: TODO
 */
public abstract class AbstractServer<T extends AbstractServer> extends AbstractEndpoint implements Server {
    private static final Logger logger = LoggerFactory.getLogger(AbstractServer.class);

    protected int port;

    public AbstractServer(Codec codec, Protocol protocol) {
        super(true, codec, protocol);
    }


    @Override
    public InetSocketAddress getLocalAddress() {
        return new InetSocketAddress(NetUtils.getLocalHost(), port);
    }

    @Override
    public void startUp() {
        super.startUp();
        long start = System.currentTimeMillis();
        try {
            doOpen();
            if (logger.isInfoEnabled()) {
                logger.info("Start " + getClass().getSimpleName() + " bind port [{}], start time {}ms",
                        port, System.currentTimeMillis() - start);
            }
        } catch (Throwable t) {
            shutDown();
            throw new RemotingException(getLocalAddress(), null, "Failed to bind " + getClass().getSimpleName()
                    + " on " + getLocalAddress() + ", cause: " + t.getMessage(), t);
        }

    }

    @Override
    public void shutDown() {
        super.shutDown();

        try {
            close();
        } catch (Throwable t) {
            logger.warn(t.getMessage(), t);
        }
    }

    @Override
    public void close() {
        if (logger.isInfoEnabled()) {
            logger.info("Close " + getClass().getSimpleName() + " bind " + getLocalAddress());
        }
        ExecutorUtil.gracefulShutdown(getProtocol().getDefaultExecutor(), 100);

        try {
            doClose();
        } catch (Throwable t) {
            logger.warn(t.getMessage(), t);
        }
    }
}
