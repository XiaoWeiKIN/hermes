package com.bolt.protocol.processor;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/21
 * @Description: TODO
 */
public abstract class AsyncUserProcessor<T> extends AbstractUserProcessorAdapter {

    public abstract void handleRequest(Object o, T request);

    @Override
    public abstract String interest();
}
