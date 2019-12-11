package com.rpc.gxc.proxy;

import com.gxc.rpc.req.RpcRequest;
import com.gxc.rpc.vo.RpcResponse;
import com.rpc.gxc.GxcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

/**
 * @author GongXincheng
 * @date 2019-12-11 10:27
 */
public class RpcProxyFactory<T> implements InvocationHandler {

    private final Logger logger = LoggerFactory.getLogger(RpcProxyFactory.class);

    private Class<T> clazz;

    public RpcProxyFactory(Class<T> clazz) {
        this.clazz = clazz;
    }

    public T getProxyService() {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class == method.getDeclaringClass()) {
            return method.invoke(args);
        }

        // 此处封装RPC入参信息
        RpcRequest request = new RpcRequest();
        request.setClassName(clazz.getName());
        request.setParameters(args);
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());

        GxcClient client = new GxcClient("127.0.0.1", 9000);
        logger.info("Proxy层调用client完成");

        RpcResponse result = client.sendRequest(request);
        if(!Objects.equals(1, result.getStatus())) {
            throw new RuntimeException("服务端返回错误");
        }
        return result.getData();
    }
}
