package com.bolt.transport;

import com.bolt.common.Url;
import io.netty.channel.WriteBufferWaterMark;

import java.net.InetSocketAddress;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/21
 * @Description: TODO
 */
public interface Endpoint {

    void close();

    Url getUrl();

    InetSocketAddress getLocalAddress();
}
