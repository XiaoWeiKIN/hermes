package com.bolt.config;

import com.bolt.common.Constants;

/**
 * @Author: wangxw
 * @DateTime: 2020/5/4
 * @Description: TODO
 */
public class BoltClientOption<T> extends BoltRemotingOption<T> {

    public static final BoltOption<String> HOST = valueOf(BoltClientOption.class, "host", "127.0.0.1");
    public static final BoltOption<Integer> PORT = valueOf(BoltClientOption.class, "port", 8091);

    // <-----通信模型------>
    public static final BoltOption<Boolean> ASYNC = valueOf(BoltClientOption.class, Constants.ASYNC_KEY, false);

    // <-----空闲检测------>
    public static final BoltOption<Integer> HEARTBEATINTERVAL = valueOf(BoltClientOption.class, Constants.HEARTBEAT_KEY, Constants.DEFAULT_HEARTBEAT);
    // 全局请求超时时间
    public static final BoltOption<Integer> TIMEOUT = valueOf(BoltClientOption.class, Constants.TIMEOUT_KEY, Constants.DEFAULT_TIMEOUT);
    // 全局连接超时时间
    public static final BoltOption<Integer> CONNECT_TIMEOUT = valueOf(BoltClientOption.class, Constants.CONNECT_TIMEOUT_KEY, Constants.DEFAULT_CONNECT_TIMEOUT);

    // <-----连接池配置------>
    public static final BoltOption<Integer> MAX_CONNECTION = valueOf(BoltClientOption.class, Constants.MAX_CONNECTION, Constants.DEFAULT_MAX_CONNECTION);
    public static final BoltOption<Integer> MAX_PENDING_ACQUIRES = valueOf(BoltClientOption.class, Constants.MAX_PENDING_ACQUIRES, Constants.DEFAULT_MAX_PENDING_ACQUIRES);
    public static final BoltOption<Long> ACQUIRE_TIMEOUT = valueOf(BoltClientOption.class, Constants.ACQUIRE_TIMEOUT, Constants.DEFAULT_ACQUIRE_TIMEOUT);
    public static final BoltOption<String> ACQUIRE_TIMEOUT_ACTION = valueOf(BoltClientOption.class, Constants.ACQUIRE_TIMEOUT_ACTION, Constants.DEFAULT_ACQUIRE_TIMEOUT_ACTION);
    public static final BoltOption<Boolean> RELEASE_HEALTH_CHECK = valueOf(BoltClientOption.class, Constants.RELEASE_HEALTH_CHECK, Constants.DEFAULT_RELEASE_HEALTH_CHECK);
    public static final BoltOption<Boolean> CONNECTION_LAST_RECENT_USED = valueOf(BoltClientOption.class, Constants.CONNECTION_LAST_RECENT_USED, Constants.DEFAULT_CONNECTION_LAST_RECENT_USED);


    protected BoltClientOption(String name, T defaultValue) {
        super(name, defaultValue);
    }
}
