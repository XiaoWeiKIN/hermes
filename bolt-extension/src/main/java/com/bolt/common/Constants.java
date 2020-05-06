package com.bolt.common;

import com.bolt.common.enums.ConnectionTimeoutAction;
import io.netty.channel.pool.FixedChannelPool;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/6
 * @Description: TODO
 */
public class Constants {
    public static final String PAYLOAD_KEY = "payload";
    public static final int DEFAULT_PAYLOAD = 8 * 1024 * 1024;                      // 8M
    public static final int DEFAULT_IO_THREADS = Math.min(Runtime.getRuntime().availableProcessors() + 1, 32);
    public static final String CONNECT_TIMEOUT_KEY = "connect.timeout";
    public static final int DEFAULT_CONNECT_TIMEOUT = 3000;
    public static final String SERIALIZATION_KEY = "serialization";
    public static final String DEFAULT_REMOTING_SERIALIZATION = "hessian2";
    public static final String MAX_CONNECTION = "max.connection";
    public static final Integer DEFAULT_MAX_CONNECTION = 1;
    public static final String MAX_PENDING_ACQUIRES = "max.pending.acquires";
    public static final Integer DEFAULT_MAX_PENDING_ACQUIRES = Integer.MAX_VALUE;
    public static final String ACQUIRE_TIMEOUT = "acquire.timeout";
    public static final Long DEFAULT_ACQUIRE_TIMEOUT = 3000L;
    public static String ACQUIRE_TIMEOUT_ACTION = "acquire.timeout.action";
    public static String DEFAULT_ACQUIRE_TIMEOUT_ACTION = ConnectionTimeoutAction.NEW.value();
    public static String RELEASE_HEALTH_CHECK = "release.health.check";
    public static boolean DEFAULT_RELEASE_HEALTH_CHECK = true;
    public static String CONNECTION_LAST_RECENT_USED = "last.recent.used";
    public static boolean DEFAULT_CONNECTION_LAST_RECENT_USED = false;

    public static final String TIMEOUT_KEY = "timeout";
    public static final int DEFAULT_TIMEOUT = 3000;
    public static final String ASYNC_KEY = "async";
    public static final String RETURN_KEY = "return";
    public static final String HEARTBEAT_KEY = "heartbeat";
    public static final int DEFAULT_HEARTBEAT = 15 * 1000;

    public static final String MAX_HEARTBEAT_COUNT = "heartbeat.times";
    public static final String HEARTBEAT_TIMEOUT_KEY = "heartbeat.timeout";
    public static final int DEFAULT_MAX_HEARTBEAT_COUNT = 3;
}
