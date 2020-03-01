package com.gxc.rpc.consumer.config;

import com.gxc.rpc.consumer.netty.NettyClientHandler;
import com.gxc.rpc.core.model.enums.ResponseStatus;
import com.gxc.rpc.core.protocol.RpcRequest;
import com.gxc.rpc.core.protocol.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author GongXincheng
 * @date 2020-03-01 16:52
 */
public class RpcProxyFactory implements InvocationHandler {

    private static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private Class<?> clazz;

    RpcProxyFactory(Class<?> clazz) {
        this.clazz = clazz;
    }

    Object getBean() {
        return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 处理Object中的方法
        if (Object.class == method.getDeclaringClass()) {
            String name = method.getName();
            if ("equals".equals(name)) {
                return proxy == args[0];
            } else if ("hashCode".equals(name)) {
                return System.identityHashCode(proxy);
            } else if ("toString".equals(name)) {
                return proxy.getClass().getName() + "@" +
                        Integer.toHexString(System.identityHashCode(proxy)) +
                        ", with InvocationHandler " + this;
            } else {
                throw new IllegalStateException(String.valueOf(method));
            }
        }

        RpcRequest request = new RpcRequest();
        request.setClassName(clazz.getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);

        NettyClientHandler clientHandler = NettyClientHandler.getInstance();
        clientHandler.setRequest(request);
        RpcResponse rpcResponse = executorService.submit(clientHandler).get();
        if (rpcResponse.getStatus() != ResponseStatus.SUCCESS.getState()) {
            throw new RuntimeException("远程调用失败：" + rpcResponse.getErrorMsg());
        }
        // 远程调用
        return rpcResponse.getData();
    }
}
