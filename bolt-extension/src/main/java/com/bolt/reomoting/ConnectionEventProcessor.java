package com.bolt.reomoting;

import com.bolt.reomoting.Connection;

import java.net.InetSocketAddress;

/**
 * @Author: wangxw
 * @DateTime: 2020/5/1
 * @Description: TODO
 */
@FunctionalInterface
public interface ConnectionEventProcessor {

    void onEvent(Connection connection);

}
