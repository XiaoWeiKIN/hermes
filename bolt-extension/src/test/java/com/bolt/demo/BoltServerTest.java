package com.bolt.demo;

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
        server.startUp();
    }
}
