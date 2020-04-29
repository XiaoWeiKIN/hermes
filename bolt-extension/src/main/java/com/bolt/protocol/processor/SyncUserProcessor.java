package com.bolt.protocol.processor;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/21
 * @Description: TODO
 */
public abstract class SyncUserProcessor<T> extends AbstractUserProcessorAdapter<T> {

    @Override
    public abstract Object handleRequest(T data) throws Exception;

    @Override
    public abstract String interest();

}
