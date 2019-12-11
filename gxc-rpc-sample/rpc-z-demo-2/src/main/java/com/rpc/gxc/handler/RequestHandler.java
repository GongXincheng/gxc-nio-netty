package com.rpc.gxc.handler;

import com.gxc.rpc.req.RpcRequest;
import com.gxc.rpc.vo.RpcResponse;
import com.rpc.gxc.registry.ServiceBeanRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author GongXincheng
 * @date 2019-12-11 11:21
 */
public class RequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    public static RpcResponse handler(RpcRequest request) {
        try {
            Object service = ServiceBeanRegistry.getService(request.getClassName());
            if (Objects.isNull(service)) {
                throw new RuntimeException("未找到指定服务");
            }
            Class<?> clazz = service.getClass();
            Method method = clazz.getMethod(request.getMethodName(), request.getParameterTypes());
            Object invoke = method.invoke(service, request.getParameters());
            return new RpcResponse(1, null, invoke);
        } catch (Exception e) {
            logger.error("RequestHandler service实现执行异常", e);
            throw new RuntimeException("RequestHandler service实现执行异常");
        }
    }
}
