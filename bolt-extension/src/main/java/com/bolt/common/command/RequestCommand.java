package com.bolt.common.command;

import com.bolt.common.Invocation;
import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/6
 * @Description: TODO
 */
@Data
public class RequestCommand extends AbstractCommand {
    private boolean twoWay = true;
    private boolean broken = false;
    private int timeout;
    private static final AtomicInteger INVOKE_ID = new AtomicInteger(0);

    public RequestCommand() {

    }

    public RequestCommand(CommandCode cmdCode) {
        super(newId(), cmdCode);
    }

    private static int newId() {
        // getAndIncrement() When it grows to MAX_VALUE, it will grow to MIN_VALUE, and the negative can be used as ID
        return INVOKE_ID.getAndIncrement();
    }
}
