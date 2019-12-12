package com.gxc.spring.component.context;

import com.gxc.spring.annotation.Bean;
import com.gxc.spring.annotation.Configuration;
import com.gxc.spring.annotation.Controller;
import com.gxc.spring.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 用于初始化Bean
 *
 * @author GongXincheng
 * @date 2019-12-12 15:10
 */
@Slf4j
public abstract class AbstractApplicationContext implements ApplicationContext {

    private static final Map<String, Object> BEAN_MAP = new HashMap<>(16);
    private static final Map<String, Object> CONTROLLER_BEAN_MAP = new HashMap<>(16);

    /**
     * 构造启动.
     */
    public AbstractApplicationContext(Class<?> config) {
        log.info("spring content start ...");
        try {
            // 如果不是配置类，报错
            if (!config.isAnnotationPresent(Configuration.class)) {
                throw new RuntimeException(String.format("%s is not a configuration bean !", config.getName()));
            }

            // 装配Bean
            registryBean(config);

            // 注入Bean
            autowireBean();

            // 启动服务
            start();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            // 关闭容器
            try {
                close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Bean注入
     */
    private void autowireBean() {
        // Controller层注入
        CONTROLLER_BEAN_MAP.forEach((beanName, bean) ->
                setBeanField(bean)
        );

        // 其他Bean注入
        BEAN_MAP.forEach((beanName, bean) ->
                setBeanField(bean)
        );
    }

    /**
     * 将Bean注入到类的字段.
     *
     * @param bean 主要注入Bean的类（属性所属的类）
     */
    private void setBeanField(Object bean) {
        Class<?> clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            // 检查是否有注入的注解
            if (field.isAnnotationPresent(Resource.class)) {
                // 获取属性名（按对象名称注入）
                String fieldName = field.getName();

                // 从两个Map中获取对象实现，并set
                Object object = getBean(fieldName);
                try {
                    field.set(bean, object);
                } catch (IllegalAccessException e) {
                    log.error("|-- Autowire bean has error in class:" + clazz.getName() + "，Field：" + fieldName);
                }
            }
        }
    }

    /**
     * 注册Bean
     *
     * @param config 配置类
     */
    private void registryBean(Class<?> config) throws Exception {
        log.info("|-------- start to registry bean, configuration bean -> {}", config.getSimpleName());
        Object configBean = config.newInstance();
        // 获取配置类中所有的方法.
        Method[] methods = config.getDeclaredMethods();
        for (Method method : methods) {
            // 找出配置@Bean注解的方法
            if (method.isAnnotationPresent(Bean.class)) {
                // 获取方法的返回值对象
                Object invoke = method.invoke(configBean);

                // 判断返回值是否是Controller，如果是则放入存Controller的Map
                Class<?> clazz = invoke.getClass();
                if (clazz.isAnnotationPresent(Controller.class)) {
                    CONTROLLER_BEAN_MAP.put(method.getName(), invoke);
                } else {
                    BEAN_MAP.put(method.getName(), invoke);
                }
                log.info("|-- registry bean：{} success !", invoke.getClass().getSimpleName());
            }
        }
        log.info("|-------- all bean has registry success !");
    }

    @Override
    public <T> T getBean(String name, Class<T> clazz) {
        Object object = getBean(name);
        if (object.getClass() == clazz) {
            return (T) object;
        }
        return (T) object;
    }

    @Override
    public Object getBean(String name) {
        Object object = BEAN_MAP.get(name);
        if (Objects.isNull(object)) {
            object = CONTROLLER_BEAN_MAP.get(name);
            if (Objects.isNull(object)) {
                return null;
            }
        }
        return object;
    }

    @Override
    public void close() throws Exception {
        BEAN_MAP.clear();
        CONTROLLER_BEAN_MAP.clear();
    }

    /**
     * 启动服务
     */
    public abstract void start() throws Exception;
}
