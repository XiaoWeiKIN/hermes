package com.bolt.codec;

import com.bolt.common.Url;
import com.bolt.common.buffer.ChannelBuffer;
import com.bolt.common.command.RemotingCommand;
import com.bolt.reomoting.Connection;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;
import lombok.Getter;

import java.util.List;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/5
 * @Description: TODO
 */
public final class CodecAdapter {
    private final Codec codec;
    @Getter
    private final InternalDecoder decoder = new InternalDecoder();
    @Getter
    private final InternalEncoder encoder = new InternalEncoder();
    private Url url;

    public CodecAdapter(Codec codec, Url url) {
        this.codec = codec;
        this.url = url;
    }

    private class InternalDecoder extends ByteToMessageDecoder {
        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            ChannelBuffer buffer = new ChannelBuffer(in);
            System.out.println("<" + in.toString(CharsetUtil.UTF_8) + ">");
            Connection connection = Connection.getOrAddConnection(ctx.channel(), url);
            System.out.println(connection.getChannel());
            try {
                Object msg = codec.decode(connection, buffer);
                if (!Codec.DecodeResult.NEED_MORE_INPUT.equals(msg)) {
                    out.add(msg);
                }
            } finally {
                // 防止内存泄漏
                Connection.removeChannelIfDisconnected(ctx.channel());

                Connection.connectionMap.forEach((k, v) -> {
                    System.out.println("k: " + k);
                });
            }
        }
    }

    private class InternalEncoder extends MessageToByteEncoder<RemotingCommand> {

        @Override
        protected void encode(ChannelHandlerContext ctx, RemotingCommand cmd, ByteBuf out) throws Exception {
            ChannelBuffer buffer = new ChannelBuffer(out);
            Connection connection = Connection.getOrAddConnection(ctx.channel(), url);
            try {
                codec.encode(connection, buffer, cmd);
            } finally {
                Connection.removeChannelIfDisconnected(ctx.channel());
            }

        }
    }


}
