package com.gxc.rpc.server.registry;

import com.gxc.rpc.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author GongXincheng
 * @date 2019-12-10 16:20
 */
public class ServiceRegistry implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);

    private static final Map<String, Object> REGISTERED_SERVICES = new HashMap<>();

    public static <T> T getService(String className) {
        return (T) REGISTERED_SERVICES.get(className);
    }

    public static void registerService(Class<?> interfaceClass, Class<?> implClass) {
        try {
            REGISTERED_SERVICES.put(interfaceClass.getName(), implClass.newInstance());
            logger.info("服务注册成功,接口:{},实现{}", interfaceClass.getName(), implClass.getName());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("服务" + implClass + "注册失败", e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        Map<String, Object> services = ctx.getBeansWithAnnotation(Service.class);
        if (Objects.nonNull(services) && services.size() > 0) {
            for (Object service : services.values()) {
                String interfaceName = service.getClass().getAnnotation(Service.class).value().getName();
                REGISTERED_SERVICES.put(interfaceName, service);
                logger.info("加载服务:{}", interfaceName);
            }
        }
    }
}
