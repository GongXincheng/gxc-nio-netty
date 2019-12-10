package com.gxc.rpc.server;

import com.gxc.rpc.interfaces.HelloService;
import com.gxc.rpc.server.registry.ServiceRegistry;
import com.gxc.rpc.server.service.impl.HelloServiceImpl;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author GongXincheng
 * @date 2019-12-10 17:37
 */
public class BioRpcServerTest {

    @Test
    public void test1() {
        // 注册服务
        ServiceRegistry.registerService(HelloService.class, HelloServiceImpl.class);

        // 启动服务
         new BioRpcServer(9000).start();
    }

}