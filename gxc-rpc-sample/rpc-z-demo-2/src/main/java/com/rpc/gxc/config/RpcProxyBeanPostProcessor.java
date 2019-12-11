package com.rpc.gxc.config;

import com.rpc.gxc.annnotation.Reference;
import com.rpc.gxc.proxy.RpcProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author GongXincheng
 * @date 2019-12-11 10:38
 */
public class RpcProxyBeanPostProcessor implements BeanPostProcessor {

    private final Logger logger = LoggerFactory.getLogger(RpcProxyBeanPostProcessor.class);

    private Map<Class<?>, Object> cache = new ConcurrentHashMap<>(16);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        try {
            // 获取Bean中所有的字段
            Class<?> clazz = bean.getClass();
            Field[] fields = clazz.getDeclaredFields();

            // 遍历所有的字段，找出带有自定义注解的字段, @Reference
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(Reference.class)) {
                    // 获取该字段的类型Class
                    Class<?> type = field.getType();
                    // 从缓存map中获取
                    Object object = cache.get(type);
                    if (Objects.isNull(object)) {
                        logger.info("in RpcProxyBeanPostProcessor get Proxy Class：{}", clazz.getName());
                        // 从代理工厂获取
                        object = new RpcProxyFactory<>(type).getProxyService();
                        cache.put(type, object);
                    }
                    field.set(bean, object);
                    return bean;
                }
            }
        } catch (Exception e) {
            logger.error("Service Bean 注入失败！", e);
        }
        return null;
    }
}
