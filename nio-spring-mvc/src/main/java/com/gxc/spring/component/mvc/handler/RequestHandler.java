package com.gxc.spring.component.mvc.handler;

import com.gxc.spring.component.model.HttpRequestAttribute;
import com.gxc.spring.model.enums.RequestMethod;

/**
 * @author GongXincheng
 * @date 2019-12-13 14:15
 */
public interface RequestHandler {

    /**
     * 处理请求.
     */
    Object doService(HttpRequestAttribute request) throws Exception;

    /**
     * 请求方法.
     */
    RequestMethod method();
}
