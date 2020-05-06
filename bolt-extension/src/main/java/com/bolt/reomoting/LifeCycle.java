package com.bolt.reomoting;

import com.bolt.common.exception.LifeCycleException;

/**
 * @Author: wangxw
 * @DateTime: 2020/3/18
 * @Description: TODO
 */
public interface LifeCycle {

    void startUp() throws LifeCycleException;

    void shutDown() throws LifeCycleException;

    boolean isStarted();
}
