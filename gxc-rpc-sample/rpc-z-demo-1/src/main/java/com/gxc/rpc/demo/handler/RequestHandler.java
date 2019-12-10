package com.gxc.rpc.demo.handler;

import com.gxc.rpc.demo.registry.BeanRegistry;
import com.gxc.rpc.req.RpcRequest;
import com.gxc.rpc.vo.RpcResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 请求处理器。
 *
 * @author GongXincheng
 * @date 2019-12-10 18:15
 */
public class RequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    public static RpcResponse handlerRequest(RpcRequest request) {

        try {
            Object object = BeanRegistry.getService(request.getClassName());

            if (Objects.isNull(object)) {
                logger.error("请求的服务未找到:{}.{}({})", request.getClassName(), request.getMethodName(),
                        StringUtils.join(request.getParameterTypes(), ", "));
                return RpcResponse.error("未知服务!");
            }

            // 获取方法
            Method method = object.getClass().getMethod(request.getMethodName(), request.getParameterTypes());

            // 返回结果
            Object invoke = method.invoke(object, request.getParameters());

            return RpcResponse.ok(invoke);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("处理请求失败", e);
            return RpcResponse.error(e.getMessage());
        }
    }

}
