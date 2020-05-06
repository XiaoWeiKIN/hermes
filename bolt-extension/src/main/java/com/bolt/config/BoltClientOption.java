package com.bolt.config;

import com.bolt.common.Constants;

/**
 * @Author: wangxw
 * @DateTime: 2020/5/4
 * @Description: TODO
 */
public class BoltClientOption<T> extends BoltRemotingOption<T> {
    // <-----通信模型------>
    public static final BoltOption<Boolean> ASYNC = valueOf(BoltClientOption.class, Constants.ASYNC_KEY, false);

    // <-----空闲检测------>
    public static final BoltOption<Integer> HEARTBEATINTERVAL = valueOf(BoltClientOption.class, Constants.HEARTBEAT_KEY, Constants.DEFAULT_HEARTBEAT);

    // <-----连接池配置------>
    public static final BoltOption<Integer> CONNECT_TIMEOUT = valueOf(BoltRemotingOption.class, Constants.CONNECT_TIMEOUT_KEY, Constants.DEFAULT_CONNECT_TIMEOUT);
    public static final BoltOption<Integer> MAX_CONNECTION = valueOf(BoltRemotingOption.class, Constants.MAX_CONNECTION, Constants.DEFAULT_MAX_CONNECTION);
    public static final BoltOption<Integer> MAX_PENDING_ACQUIRES = valueOf(BoltRemotingOption.class, Constants.MAX_PENDING_ACQUIRES, Constants.DEFAULT_MAX_PENDING_ACQUIRES);
    public static final BoltOption<Long> ACQUIRE_TIMEOUT = valueOf(BoltRemotingOption.class, Constants.ACQUIRE_TIMEOUT, Constants.DEFAULT_ACQUIRE_TIMEOUT);
    public static final BoltOption<String> ACQUIRE_TIMEOUT_ACTION = valueOf(BoltRemotingOption.class, Constants.ACQUIRE_TIMEOUT_ACTION, Constants.DEFAULT_ACQUIRE_TIMEOUT_ACTION);
    public static final BoltOption<Boolean> RELEASE_HEALTH_CHECK = valueOf(BoltRemotingOption.class, Constants.RELEASE_HEALTH_CHECK, Constants.DEFAULT_RELEASE_HEALTH_CHECK);
    public static final BoltOption<Boolean> CONNECTION_LAST_RECENT_USED = valueOf(BoltRemotingOption.class, Constants.CONNECTION_LAST_RECENT_USED, Constants.DEFAULT_CONNECTION_LAST_RECENT_USED);


    protected BoltClientOption(String name, T defaultValue) {
        super(name, defaultValue);
    }
}
