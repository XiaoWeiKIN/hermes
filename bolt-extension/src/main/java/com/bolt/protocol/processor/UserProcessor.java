package com.bolt.protocol.processor;

import com.bolt.common.command.CommandCode;
import com.bolt.common.extension.Extension;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/19
 * @Description: TODO
 */
@Extension
public interface UserProcessor<T> {

    String interest();

    CommandCode cmdCode();

    ExecutorService getExecutor();

    boolean processInIOThread();

    Object handleRequest(T request) throws Exception;
}
