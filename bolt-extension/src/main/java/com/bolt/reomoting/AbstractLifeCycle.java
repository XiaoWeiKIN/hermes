package com.bolt.reomoting;

import com.bolt.common.exception.LifeCycleException;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: wangxw
 * @DateTime: 2020/3/18
 * @Description: TODO
 */
public abstract class AbstractLifeCycle implements LifeCycle {

    private final AtomicBoolean isStarted = new AtomicBoolean(false);
    private final boolean serverSide;
    public AbstractLifeCycle(boolean serverSide) {
        this.serverSide = serverSide;
    }

    @Override
    public void startUp() {
        if (isStarted.compareAndSet(false, true)) {
            return;
        }
        throw new LifeCycleException(serverSide ? "Server" : "Client" + " component has started");

    }

    @Override
    public void shutDown() {
        if (isStarted.compareAndSet(true, false)) {
            return;
        }
        throw new LifeCycleException(serverSide ? "Server" : "Client" + " component has started");
    }


    @Override
    public boolean isStarted() {
        return isStarted.get();
    }

    @Override
    public boolean isServerSide() {
        return serverSide;
    }
}
