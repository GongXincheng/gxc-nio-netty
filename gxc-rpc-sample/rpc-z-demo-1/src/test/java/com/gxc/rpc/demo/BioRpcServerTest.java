package com.gxc.rpc.demo;

import com.gxc.rpc.demo.factory.RpcProxyFactory;
import com.gxc.rpc.demo.registry.BeanRegistry;
import com.gxc.rpc.demo.service.HelloServiceImpl;
import com.gxc.rpc.interfaces.HelloService;
import org.junit.Test;

/**
 * @author GongXincheng
 * @date 2019-12-10 19:39
 */
public class BioRpcServerTest {

    /**
     * 服务端
     */
    @Test
    public void start() {
        BeanRegistry.registerService(HelloService.class, HelloServiceImpl.class);
        BioRpcServer server = new BioRpcServer(9000);
        server.start();
    }

    /**
     * 客户端
     */
    @Test
    public void sendRequest() {
        HelloService helloService = new RpcProxyFactory<>(HelloService.class).getProxyService();
        String result = helloService.sayHello("Hello GXC");
        System.out.println(result);
    }
}