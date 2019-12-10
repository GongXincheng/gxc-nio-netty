package com.gxc.rpc.server.handler;

import com.gxc.rpc.server.registry.ServiceRegistry;
import com.gxc.rpc.req.RpcRequest;
import com.gxc.rpc.vo.RpcResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author GongXincheng
 * @date 2019-12-10 16:25
 */
public class RequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    public static RpcResponse handleRequest(RpcRequest request) {
        try {
            // 获取服务
            Object service = ServiceRegistry.getService(request.getClassName());

            if (Objects.nonNull(service)) {
                Class<?> clazz = service.getClass();

                // 获取方法
                Method method = clazz.getMethod(request.getMethodName(), request.getParameterTypes());

                // 执行方法
                Object result = method.invoke(service, request.getParameters());

                // 写回结果
                return RpcResponse.ok(result);
            } else {
                logger.error("请求的服务未找到:{}.{}({})", request.getClassName(), request.getMethodName(),
                        StringUtils.join(request.getParameterTypes(), ", "));
                return RpcResponse.error("未知服务!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("处理请求失败", e);
            return RpcResponse.error(e.getMessage());
        }
    }

}
