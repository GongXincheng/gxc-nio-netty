package com.gxc.rpc.server.service.impl;

import com.gxc.rpc.annotation.Service;
import com.gxc.rpc.interfaces.HelloService;

/**
 * @author GongXincheng
 * @date 2019-12-10 17:38
 */
@Service(HelloService.class)
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return "服务返回：Hello " + name;
    }
}
