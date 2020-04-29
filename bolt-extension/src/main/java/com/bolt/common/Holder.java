package com.bolt.common;

/**
 * @Author: wangxw
 * @DateTime: 2020/3/25
 * @Description: TODO
 */
public class Holder<T> {
    private volatile T value;

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }
}
