package com.bolt.common;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/18
 * @Description: TODO
 */
public final class Version {
    public static final String DEFAULT_BOLT_PROTOCOL_VERSION = "1.0.0";
    private Version(){

    }
    public static String getProtocolVersion() {
        return DEFAULT_BOLT_PROTOCOL_VERSION;
    }
}
