package com.bolt.config;

import com.bolt.common.Constants;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/10
 * @Description: TODO
 */
public class BoltRemotingOption<T> extends BoltOption<T> {
    public static final BoltOption<String> SERIALIZATION = valueOf(BoltRemotingOption.class, Constants.SERIALIZATION_KEY, Constants.DEFAULT_REMOTING_SERIALIZATION);
    protected BoltRemotingOption(String name, T defaultValue) {
        super(name, defaultValue);
    }
}
