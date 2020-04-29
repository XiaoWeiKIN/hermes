package com.bolt.config;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/9
 * @Description: TODO
 */
public class BoltOptions {

    private final ConcurrentMap<BoltOption<?>, Object> options = new ConcurrentHashMap<>();

    public <T> BoltOptions option(BoltOption<T> option, T value) {
        if (value == null) {
            options.remove(option);
        } else {
            options.put(option, value);
        }
        return this;
    }

    public <T> T option(BoltOption<T> option) {
        Object value = options.get(option);
        if (value == null) {
            options.put(option, option.defaultValue());
            value = option.defaultValue();

        }
        return value == null ? null : (T) value;
    }

    public Map<String, Object> options(Class<? extends BoltOption> type) {
        return options().entrySet()
                .stream()
                .filter(option -> option.getKey().type().equals(type))
                .collect(Collectors.toMap(e -> e.getKey().name(), Map.Entry::getValue));
    }

    final Map<BoltOption<?>, Object> options() {
        synchronized (options) {
            return copiedMap(options);
        }
    }

    static <K, V> Map<K, V> copiedMap(Map<K, V> map) {
        if (map.isEmpty()) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(new HashMap<K, V>(map));
    }
}
