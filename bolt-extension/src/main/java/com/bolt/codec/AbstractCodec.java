package com.bolt.codec;

import com.bolt.common.Constants;
import com.bolt.reomoting.Connection;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.TooLongFrameException;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/6
 * @Description: TODO
 */
@Slf4j
public abstract class AbstractCodec implements Codec {

    public static void checkPlayLoad(Connection connection, long length) {
        int payload = Constants.DEFAULT_PAYLOAD;
        if (connection != null && connection.getUrl() != null) {
            payload = connection.getUrl().getParameter(Constants.PAYLOAD_KEY, Constants.DEFAULT_PAYLOAD);
        }
        if (payload > 0 && length > payload) {
            TooLongFrameException e = new TooLongFrameException("Data length too large: " + length + ", max payload: " + payload);
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    protected void checkMagic(byte[] header, int readable) throws DecoderException {
        if (readable < 1) {
            return;
        }
        if (header[0] != getMagicCode()) {
            throw new DecoderException("Magic code check failed");
        }
    }

    protected abstract byte getMagicCode();

}
