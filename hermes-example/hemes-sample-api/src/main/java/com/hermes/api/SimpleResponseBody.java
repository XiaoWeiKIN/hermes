package com.hermes.api;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: wangxw
 * @DateTime: 2020/5/8
 * @Description: TODO
 */
@AllArgsConstructor
@Data
public class SimpleResponseBody implements Serializable {
    private String data;
}
