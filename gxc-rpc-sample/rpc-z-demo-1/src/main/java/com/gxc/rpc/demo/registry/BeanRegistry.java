package com.gxc.rpc.demo.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Bean注册工厂.
 *
 * @author GongXincheng
 * @date 2019-12-10 18:16
 */
public class BeanRegistry {

    private static final Logger logger = LoggerFactory.getLogger(BeanRegistry.class);

    private static final Map<String, Object> BEAN_MAP = new HashMap<>(16);

    public static <T> T getService(String name) {
        return (T) BEAN_MAP.get(name);
    }

    public static void registerService(Class<?> interfaceClass, Class<?> implClass) {
        try {
            BEAN_MAP.put(interfaceClass.getName(), implClass.newInstance());
            logger.info("服务注册成功,接口:{},实现{}", interfaceClass.getName(), implClass.getName());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("服务" + implClass + "注册失败", e);
        }
    }


}
