package com.bolt.codec;

import com.bolt.common.buffer.ChannelBuffer;
import com.bolt.common.command.RemotingCommand;
import com.bolt.reomoting.Connection;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/4
 * @Description: TODO
 */
public interface Codec {

    void encode(Connection connection, ChannelBuffer buffer, RemotingCommand msg) throws EncoderException;

    Object decode(Connection connection, ChannelBuffer buffer) throws DecoderException;

    enum DecodeResult {
        NEED_MORE_INPUT
    }
}
