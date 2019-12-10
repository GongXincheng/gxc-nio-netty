package com.gxc.rpc.demo.service;

import com.gxc.rpc.interfaces.HelloService;

/**
 * @author GongXincheng
 * @date 2019-12-10 19:40
 */
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return "Hello：" + name;
    }
}
