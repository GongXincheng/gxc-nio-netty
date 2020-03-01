package com.gxc.provider.user.handler;

import com.gxc.provider.user.register.ServiceRegistry;
import com.gxc.rpc.core.protocol.RpcRequest;
import com.gxc.rpc.core.protocol.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author GongXincheng
 * @date 2020-03-01 22:13
 */
@Slf4j
public class RequestHandler {

    /**
     * 执行实现类方法.
     *
     * @param request RpcRequest
     * @return RpcResponse
     */
    public static RpcResponse<Object> process(RpcRequest request) {
        try {
            Object serviceImpl = ServiceRegistry.getService(request.getClassName());
            if (Objects.isNull(serviceImpl)) {
                throw new RuntimeException(String.format("请求的服务未找到:[%s].%s(%s)", request.getClassName(), request.getMethodName(),
                        StringUtils.join(request.getParameterTypes(), ", ")));
            }

            Class<?> implClass = serviceImpl.getClass();

            Method method = implClass.getMethod(request.getMethodName(), request.getParameterTypes());

            Object invoke = method.invoke(serviceImpl, request.getParameters());
            return RpcResponse.success(invoke);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return RpcResponse.error(e.getMessage());
        }
    }

}
