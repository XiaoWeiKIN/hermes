package com.bolt.transport;

import com.bolt.codec.Codec;
import com.bolt.reomoting.AbstractLifeCycle;
import com.bolt.common.Url;
import com.bolt.config.BoltGenericOption;
import com.bolt.config.BoltOption;
import com.bolt.config.BoltOptions;
import com.bolt.config.Configurable;
import com.bolt.protocol.Protocol;
import io.netty.channel.WriteBufferWaterMark;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/9
 * @Description: TODO
 */
public abstract class AbstractEndpoint extends AbstractLifeCycle implements Endpoint, Configurable {
    private static final Logger logger = LoggerFactory.getLogger(BoltServer.class);
    protected final BoltOptions options = new BoltOptions();
    private Codec codec;
    private Protocol protocol;
    private volatile Url url;

    protected abstract void doOpen() throws Throwable;

    protected abstract void doClose() throws Throwable;

    public AbstractEndpoint(Codec codec, Protocol protocol) {
        this(false, codec, protocol);
    }

    public AbstractEndpoint(boolean serverSide, Codec codec, Protocol protocol) {
        super(serverSide);
        this.codec = codec;
        this.protocol = protocol;
    }

    @Override
    public <T> Configurable option(BoltOption<T> option, T value) {
        options.option(option, value);
        return this;
    }

    @Override
    public <T> T option(BoltOption<T> option) {
        return options.option(option);
    }

    protected Map<String, Object> options(Class<? extends BoltOption> type) {
        return options.options(type);
    }

    protected Codec getCodec() {
        return codec;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    protected void setUrl(Url url) {
        this.url = url;
    }

    @Override
    public Url getUrl() {
        return url;
    }

    public WriteBufferWaterMark initWriteBufferWaterMark() {
        Integer lowWaterMark = this.option(BoltGenericOption.NETTY_BUFFER_LOW_WATER_MARK);
        Integer highWaterMark = this.option(BoltGenericOption.NETTY_BUFFER_HIGH_WATER_MARK);
        String prefix = isServerSide() ? "[server side]" : "[client side]";
        if (lowWaterMark > highWaterMark) {
            throw new IllegalArgumentException(
                    String.format(prefix + " bolt netty high water mark {%s}" +
                                    " should not be smaller than low water mark {%s} bytes)",
                            highWaterMark, lowWaterMark));
        } else {
            logger.info(prefix + " bolt netty low water mark is {} bytes, high water mark is {} bytes",
                    lowWaterMark, highWaterMark);
        }
        return new WriteBufferWaterMark(lowWaterMark, highWaterMark);
    }

}
