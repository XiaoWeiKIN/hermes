package com.bolt.common.command;

import com.bolt.common.enums.ResponseStatus;
import lombok.Data;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/6
 * @Description: TODO
 */
@Data
public class ResponseCommand extends AbstractCommand {
    private ResponseStatus status;
    private String errorMessage;

    public ResponseCommand(CommandCode cmdCode) {
        super(cmdCode);
    }

    public ResponseCommand(int id, CommandCode cmdCode) {
        super(id, cmdCode);
    }

    @Override
    public String toString() {

        return "ResponseCommand{status=" + status +
                " id=" + getId() +
                " cmdCode=" + getCmdCode() +
                " isHeartBeat" + isHeartbeat() + "}";
    }
}
