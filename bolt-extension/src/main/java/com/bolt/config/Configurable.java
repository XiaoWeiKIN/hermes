package com.bolt.config;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/10
 * @Description: TODO
 */
public interface Configurable {

    <T> Configurable option(BoltOption<T> option, T value);

    <T> T option(BoltOption<T> option);

}
