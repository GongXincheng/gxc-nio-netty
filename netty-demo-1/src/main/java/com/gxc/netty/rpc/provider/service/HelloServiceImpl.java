package com.gxc.netty.rpc.provider.service;

import com.gxc.netty.rpc.common.HelloService;

/**
 * @author GongXincheng
 * @date 2020-02-29 22:06
 */
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(String message) {
        return "RPC result：" + message;
    }

}
