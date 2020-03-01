package com.gxc.spring.lifecycle;

/**
 * 后处理Bean
 *
 * @author GongXincheng
 * @date 2020-03-01 03:05
 */
public interface BeanPostProcessor {

    /**
     * postProcessBeforeInitialization
     *
     * @param bean
     * @param beanName
     * @return
     * @throws Exception
     */
    default Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }


    /**
     * postProcessAfterInitialization
     *
     * @param bean
     * @param beanName
     * @return
     * @throws Exception
     */
    default Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }

}
