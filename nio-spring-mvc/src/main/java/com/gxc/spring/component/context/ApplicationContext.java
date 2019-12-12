package com.gxc.spring.component.context;

/**
 * @author GongXincheng
 * @date 2019-12-12 14:40
 */
public interface ApplicationContext {

    /**
     * 从Spring容器中获取Bean.
     *
     * @param name  bean的名称
     * @param clazz bean的类型
     * @return T
     */
    <T> T getBean(String name, Class<T> clazz);

    /**
     * 从Spring容器中获取Bean.
     *
     * @param name bean的名称
     * @return Object
     */
    Object getBean(String name);

    /**
     * 关闭服务
     */
    void close() throws Exception;
}
