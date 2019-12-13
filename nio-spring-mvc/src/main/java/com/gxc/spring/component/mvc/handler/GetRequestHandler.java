package com.gxc.spring.component.mvc.handler;

import com.gxc.spring.component.model.HttpRequestAttribute;
import com.gxc.spring.model.enums.RequestMethod;

/**
 * @author GongXincheng
 * @date 2019-12-13 14:32
 */
public class GetRequestHandler extends AbstractRequestHandler {

    @Override
    public Object doService(HttpRequestAttribute request) throws Exception {
        return getHandlerMethod(request.getMethod(), request.getUri());
    }

    @Override
    public RequestMethod method() {
        return RequestMethod.GET;
    }
}
