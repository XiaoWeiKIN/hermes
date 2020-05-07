package com.bolt.demo;

import com.bolt.common.enums.ConnectionEventType;
import com.bolt.config.BoltServerOption;
import com.bolt.transport.BoltServer;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/18
 * @Description: TODO
 */
public class BoltServerTest {
    public static void main(String[] args) {
        BoltServer server = new BoltServer();
        server.option(BoltServerOption.PORT,9091);
        server.addConnectionEventProcessor(ConnectionEventType.CONNECT,((connection) -> {
            // 并发控制，连接统计等
        }));
        server.startUp();
    }
}
