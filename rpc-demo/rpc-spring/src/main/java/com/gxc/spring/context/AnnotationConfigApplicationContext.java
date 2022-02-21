package com.gxc.spring.context;

import com.gxc.spring.annotation.Bean;
import com.gxc.spring.annotation.Configuration;
import com.gxc.spring.annotation.Resource;
import com.gxc.spring.lifecycle.BeanPostProcessor;
import com.gxc.spring.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author GongXincheng
 * @date 2020-03-01 03:09
 */
@Slf4j
public class AnnotationConfigApplicationContext implements ApplicationContext {

    /**
     * Bean容器.
     */
    private static final Map<String, Object> BEAN_MAP = new ConcurrentHashMap<>(16);

    /**
     * 构造方法
     *
     * @param configClass 配置类
     * @throws Exception Exception
     */
    public AnnotationConfigApplicationContext(Class<?> configClass) throws Exception {
        List<Class<?>> classList = Arrays.asList(configClass.getInterfaces());
        if (!configClass.isAnnotationPresent(Configuration.class) || !classList.contains(ContainerRun.class)) {
            throw new RuntimeException(String.format("%s is not a configuration bean !", configClass.getName()));
        }

        // 注册Bean
        registerBean(configClass);

        // 注入Bean
        autowireBean();

        Object configBean = configClass.newInstance();
        Method start = configClass.getMethod("start");
        start.invoke(configBean);
    }

    /**
     * 依赖注入.
     */
    private void autowireBean() {
        BEAN_MAP.forEach((k, v) -> {
            setBeanField(v);
        });
    }

    /**
     * 依赖注入(按名称注入)
     */
    private void setBeanField(Object v) {
        Field[] fields = v.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (!field.isAnnotationPresent(Resource.class)) {
                continue;
            }
            // 按名称注入
            String fieldName = field.getName();
            Object bean = this.getBean(fieldName);
            try {
                field.set(bean, bean);
            } catch (IllegalAccessException e) {
                log.error("|-- Autowire bean has error in class:" + v.getClass().getName() + "，Field：" + fieldName);
            }
            log.info("|-- autowired bean：{}，field：{}", bean.getClass().getSimpleName(), fieldName);
        }
    }

    /**
     * 将Bean注册到容器
     *
     * @param configClass 配置类
     */
    private void registerBean(Class<?> configClass) throws Exception {
        log.info("|-------- start to registry bean --------");
        Object configObject = configClass.newInstance();

        Method[] methods = configClass.getDeclaredMethods();
        for (Method method : methods) {
            if (!method.isAnnotationPresent(Bean.class)) {
                continue;
            }
            // 执行该方法，返回值为Bean
            Object bean = method.invoke(configObject);
            BEAN_MAP.put(method.getName(), bean);
            log.info("|-- registry bean：{} success ! beanName: {}", bean.getClass().getSimpleName(), method.getName());
        }

        // 后处理bean.
        this.postProcessorBean();

        log.info("|-------- all bean has registry success ! \n");
    }

    /**
     * 后处理bean.
     */
    private void postProcessorBean() throws Exception {
        // TODO：后处理Bean
        BeanPostProcessor postProcessor = (BeanPostProcessor) BEAN_MAP.get(StringUtils.getBeanName(BeanPostProcessor.class));
        if (Objects.isNull(postProcessor)) {
            log.info("|-- don't have post processor bean");
            return;
        }

        for (Map.Entry<String, Object> entry : BEAN_MAP.entrySet()) {
            String beanName = entry.getKey();
            Object bean = entry.getValue();

            postProcessor.postProcessBeforeInitialization(bean, beanName);
            // TODO：what you want to do;
            postProcessor.postProcessAfterInitialization(bean, beanName);
        }
    }


    @Override
    public Object getBean(String name) {
        Object result = BEAN_MAP.get(name);
        if (Objects.isNull(result)) {
            return null;
        }
        return result;
    }

    /**
     * 获取容器中所有Bean.
     *
     * @return Map<String, Object>
     */
    public static Map<String, Object> getAllBean() {
        return BEAN_MAP;
    }

}
