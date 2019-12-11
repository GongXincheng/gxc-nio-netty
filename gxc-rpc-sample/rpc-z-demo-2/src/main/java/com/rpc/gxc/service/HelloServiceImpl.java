package com.rpc.gxc.service;

import com.gxc.rpc.interfaces.HelloService;
import com.rpc.gxc.annnotation.RpcService;
import org.springframework.stereotype.Service;

/**
 * @author GongXincheng
 * @date 2019-12-11 10:44
 */
@RpcService(value = HelloService.class)
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return "Hello " + name;
    }
}
