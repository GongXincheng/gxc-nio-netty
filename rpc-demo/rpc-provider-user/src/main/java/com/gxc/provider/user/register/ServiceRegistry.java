package com.gxc.provider.user.register;


import com.gxc.rpc.core.annotation.Service;
import com.gxc.spring.context.AnnotationConfigApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author GongXincheng
 * @date 2019-12-10 16:20
 */
public class ServiceRegistry {

    private static final Map<String, Object> REGISTERED_SERVICES = new HashMap<>();

    /**
     * 获取接口实现类
     *
     * @param className 接口全限定名
     * @return 接口实现类
     */
    public static Object getService(String className) {
        // 先从缓存中获取
        Object result = REGISTERED_SERVICES.get(className);
        if (Objects.nonNull(result)) {
            return result;
        }

        // 获取容器中所有 Service 注解的类
        Map<String, Object> allBean = AnnotationConfigApplicationContext.getAllBean();
        for (Map.Entry<String, Object> entry : allBean.entrySet()) {
            Object bean = entry.getValue();

            Class<?> beanClass = bean.getClass();
            if (!beanClass.isAnnotationPresent(Service.class)) {
                continue;
            }

            Service serviceAnnotation = beanClass.getAnnotation(Service.class);
            Class<?> value = serviceAnnotation.value();
            REGISTERED_SERVICES.put(value.getName(), bean);
        }
        return REGISTERED_SERVICES.get(className);
    }

}
