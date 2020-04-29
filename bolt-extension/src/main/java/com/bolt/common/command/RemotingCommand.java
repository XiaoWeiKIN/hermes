package com.bolt.common.command;

import com.bolt.common.Invocation;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/6
 * @Description: TODO
 */
public interface RemotingCommand {

    CommandCode getCmdCode();

    int getId();

    Invocation getInvocation();

}
