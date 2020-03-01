package com.gxc.spring.utils;

/**
 * @author GongXincheng
 * @date 2020-03-01 16:23
 */
public class StringUtils {

    /**
     * 根据Class获取类名 并将第一个字母小写.
     *
     * @param clazz Class
     * @return beanName
     */
    public static String getBeanName(Class<?> clazz) {
        String name = clazz.getSimpleName();
        String first = name.substring(0, 1).toLowerCase();
        String prefix = name.substring(1);
        return first + prefix;
    }

}
