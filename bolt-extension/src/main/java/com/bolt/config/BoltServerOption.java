package com.bolt.config;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/21
 * @Description: TODO
 */
public class BoltServerOption<T> extends BoltOption<T> {
    public static final BoltOption<Integer> PORT = valueOf("bolt.server.port",8090);

    protected BoltServerOption(String name, T defaultValue) {
        super(name, defaultValue);
    }
}
