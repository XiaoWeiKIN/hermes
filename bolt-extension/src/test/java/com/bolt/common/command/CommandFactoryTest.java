package com.bolt.common.command;

import com.bolt.common.Invocation;
import com.bolt.common.enums.CommandCodeEnum;
import jdk.nashorn.internal.objects.annotations.Setter;
import lombok.Data;
import lombok.Getter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/11
 * @Description: TODO
 */
@RunWith(JUnit4.class)
public class CommandFactoryTest {

    @Test
    public void createRequestCommand() {
        RequestBody requestBody = new RequestBody(CommandCodeEnum.GENERAL_CMD);
        requestBody.setName("zhang san");
        CommandFactory factory = new CommandFactory();
        RequestCommand requestCommand = factory.createRequest(requestBody);
        Invocation data = requestCommand.getInvocation();
        System.out.println(requestCommand.getCmdCode());
        System.out.println(data.toString());
    }

    public class RequestBody extends Command {
        @Getter
        private String name;

        public RequestBody(CommandCode cmdCode) {
            super(cmdCode);
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}