package com.bolt.common;

import lombok.Data;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/6
 * @Description: TODO
 */
@Data
public class Invocation<T> {
    private String className;
    private T data;


}
