package com.gxc.rpc.client;

import com.gxc.rpc.client.factory.RpcProxyFactory;
import com.gxc.rpc.interfaces.HelloService;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author GongXincheng
 * @date 2019-12-10 17:42
 */
public class BioRpcClientTest {

    @Test
    public void test1() throws IOException {
        // 通过代理工厂，获取服务
        HelloService helloService = new RpcProxyFactory<>(HelloService.class).getProxyObject();

        String result = helloService.sayHello("gongxincheng");
        System.out.println(result);
    }

}