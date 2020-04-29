package com.bolt.common.enums;

import lombok.AllArgsConstructor;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/17
 * @Description: TODO
 */
@AllArgsConstructor
public enum ConnectionTimeoutAction {
    NEW("new"),
    FIAL("fail");
    private String value;

    public String value() {
        return this.value;
    }
}
