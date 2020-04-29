package com.bolt.common;

import com.bolt.common.exception.SerializationException;
import io.netty.handler.codec.DecoderException;

import java.io.IOException;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/20
 * @Description: TODO
 */
public interface Decdeable {

    void decodeClassName() throws DecoderException;

    void decodeData() throws DecoderException;

}
