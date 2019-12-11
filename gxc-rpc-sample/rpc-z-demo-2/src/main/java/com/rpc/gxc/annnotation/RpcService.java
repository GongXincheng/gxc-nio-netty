package com.rpc.gxc.annnotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Service实现类注册到spring容器的Bean
 *
 * @author GongXincheng
 * @date 2019-12-11 10:02
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {

    /**
     * 接口名称
     */
    Class<?> value();

}
