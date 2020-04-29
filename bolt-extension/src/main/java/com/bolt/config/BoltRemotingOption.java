package com.bolt.config;

import com.bolt.common.Constants;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/10
 * @Description: TODO
 */
public final class BoltRemotingOption<T> extends BoltOption<T> {
    public static final BoltOption<String> SERIALIZATION = valueOf(BoltRemotingOption.class, Constants.SERIALIZATION_KEY, Constants.DEFAULT_REMOTING_SERIALIZATION);
    public static final BoltOption<Integer> CONNECT_TIMEOUT = valueOf(BoltRemotingOption.class, Constants.CONNECT_TIMEOUT_KEY, Constants.DEFAULT_CONNECT_TIMEOUT);
    public static final BoltOption<Integer> MAX_CONNECTION = valueOf(BoltRemotingOption.class, Constants.MAX_CONNECTION, Constants.DEFAULT_MAX_CONNECTION);
    public static final BoltOption<Integer> MAX_PENDING_ACQUIRES = valueOf(BoltRemotingOption.class, Constants.MAX_PENDING_ACQUIRES, Constants.DEFAULT_MAX_PENDING_ACQUIRES);
    public static final BoltOption<Long> ACQUIRE_TIMEOUT = valueOf(BoltRemotingOption.class, Constants.ACQUIRE_TIMEOUT, Constants.DEFAULT_ACQUIRE_TIMEOUT);
    public static final BoltOption<String> ACQUIRE_TIMEOUT_ACTION = valueOf(BoltRemotingOption.class, Constants.ACQUIRE_TIMEOUT_ACTION, Constants.DEFAULT_ACQUIRE_TIMEOUT_ACTION);
    public static final BoltOption<Boolean> RELEASE_HEALTH_CHECK = valueOf(BoltRemotingOption.class, Constants.RELEASE_HEALTH_CHECK, Constants.DEFAULT_RELEASE_HEALTH_CHECK);
    public static final BoltOption<Boolean> CONNECTION_LAST_RECENT_USED = valueOf(BoltRemotingOption.class, Constants.CONNECTION_LAST_RECENT_USED, Constants.DEFAULT_CONNECTION_LAST_RECENT_USED);
    public static final BoltOption<Boolean> ASYNC = valueOf(BoltRemotingOption.class, Constants.ASYNC_KEY, false);

    protected BoltRemotingOption(String name, T defaultValue) {
        super(name, defaultValue);
    }
}
