package com.gxc.rpc.consumer.config;

import com.gxc.rpc.core.annotation.Reference;
import com.gxc.spring.lifecycle.BeanPostProcessor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author GongXincheng
 * @date 2020-03-01 16:41
 */
@Slf4j
public class RpcProxyBeanPostProcessor implements BeanPostProcessor {

    private final Map<Class<?>, Object> cache = new HashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        Class<?> beanClass = bean.getClass();

        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (!field.isAnnotationPresent(Reference.class)) {
                continue;
            }

            Class<?> typeClass = field.getType();

            Object proxyBean = cache.get(typeClass);
            if (Objects.isNull(proxyBean)) {
                proxyBean = new RpcProxyFactory(typeClass).getBean();
                cache.put(typeClass, proxyBean);
            }

            field.set(bean, proxyBean);
        }
        return bean;
    }
}
