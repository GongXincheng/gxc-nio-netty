package com.gxc.spring.component.mvc.handler;

import com.gxc.spring.model.enums.RequestMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author GongXincheng
 * @date 2019-12-13 14:37
 */
public class RequestHandlerFactory {

    private static final List<RequestHandler> HANDLER_LIST = new ArrayList<>();

    static {
        HANDLER_LIST.add(new GetRequestHandler());
        HANDLER_LIST.add(new PostRequestHandler());
    }

    /**
     * 获取请求处理器
     *
     * @param method 请求method
     * @return RequestHandler
     */
    public static RequestHandler getHandler(String method) {
        return HANDLER_LIST.stream().filter(e -> Objects.equals(e.method(), RequestMethod.codeOf(method.toUpperCase())))
                .findFirst().orElseThrow(() -> new RuntimeException("not find request handler!"));
    }


}
