package com.gxc.spring.context;

/**
 * @author GongXincheng
 * @date 2020-03-01 03:05
 */
public interface ApplicationContext {

    /**
     * getBean
     *
     * @param name name
     * @return Object
     */
    Object getBean(String name);
}
