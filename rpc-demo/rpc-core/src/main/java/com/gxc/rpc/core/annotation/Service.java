package com.gxc.rpc.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RPC Service 注解
 *
 * @author GongXincheng
 * @date 2020-03-01 02:52
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {

    /**
     * 接口名称 * @return
     */
    Class<?> value();
}