package com.bolt.common.enums;

import com.bolt.common.command.CommandCode;
import lombok.AllArgsConstructor;

/**
 * @Author: wangxw
 * @DateTime: 2020/3/30
 * @Description: TODO
 */
@AllArgsConstructor
public enum CommandCodeEnum implements CommandCode {
    GENERAL_CMD((short) 0);

    private short value;

    @Override
    public short getValue() {
        return this.value;
    }

    public static CommandCodeEnum toEnum(short value) {
        for (CommandCodeEnum cmd : values()) {
            if (value == cmd.getValue()) {
                return cmd;
            }
        }
        throw new IllegalArgumentException("Unknown command code value: " + value);
    }
}
