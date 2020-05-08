package com.hermes.autoconfigure;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @Author: wangxw
 * @DateTime: 2020/5/8
 * @Description: TODO
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(HermesAutoConfiguration.class)
@Documented
@Inherited
public @interface EnableHermes {
}
