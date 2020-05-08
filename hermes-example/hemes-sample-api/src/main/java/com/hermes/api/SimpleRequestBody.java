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
public class SimpleRequestBody implements Serializable {
    private String name;
    private int age;
    private long phone;
}
