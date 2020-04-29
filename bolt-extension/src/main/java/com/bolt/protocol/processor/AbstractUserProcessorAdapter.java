package com.bolt.protocol.processor;

import com.bolt.common.command.CommandCode;
import com.bolt.common.enums.CommandCodeEnum;
import com.bolt.util.NamedThreadFactory;

import java.util.concurrent.*;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/20
 * @Description: TODO
 */
public abstract class AbstractUserProcessorAdapter<T> implements UserProcessor<T> {

    private ExecutorService executor;

    @Override
    public ExecutorService getExecutor() {
        return executor;
    }

    @Override
    public boolean processInIOThread() {
        return false;
    }

    @Override
    public CommandCode cmdCode() {
        return CommandCodeEnum.GENERAL_CMD;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

}
