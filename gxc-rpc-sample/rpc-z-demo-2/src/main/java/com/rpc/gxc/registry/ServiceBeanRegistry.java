package com.rpc.gxc.registry;

import com.rpc.gxc.annnotation.RpcService;
import com.rpc.gxc.interfaces.RpcServer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * @author GongXincheng
 * @date 2019-12-11 11:23
 */
public class ServiceBeanRegistry implements ApplicationContextAware {

    private static final Map<String, Object> map = new HashMap<>(16);

    public static Object getService(String name) {
        return map.get(name);
    }

    public static void registryBean(Class<?> interfaceName, Class<?> implName) throws Exception {
        map.put(interfaceName.getName(), implName.newInstance());
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        Map<String, Object> serviceMap = ctx.getBeansWithAnnotation(RpcService.class);
        for (Map.Entry<String, Object> entry : serviceMap.entrySet()) {
            Object value = entry.getValue();
            RpcService annotation = value.getClass().getAnnotation(RpcService.class);
            Class<?> clazz = annotation.value();
            map.put(clazz.getName(), value);
        }
    }
}
