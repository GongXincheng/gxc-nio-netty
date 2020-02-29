package com.gxc.netty.rpc.consumer;

import com.gxc.netty.rpc.common.HelloService;
import com.gxc.netty.rpc.netty.Constant;
import com.gxc.netty.rpc.netty.NettyClient;

/**
 * @author GongXincheng
 * @date 2020-03-01 00:46
 */
public class ClientBootstrap {

    public static void main(String[] args) {

        // 创建消费者
        NettyClient nettyClient = new NettyClient();

        HelloService helloService = (HelloService) nettyClient.getBean(HelloService.class, Constant.PROTOCOL_PREFIX);

        String result = helloService.hello("GongXincheng");
        System.out.println(result);

        String result1 = helloService.hello("GongXincheng1");
        System.out.println(result1);

        String result2 = helloService.hello("GongXincheng2");
        System.out.println(result2);

        String result3 = helloService.hello("GongXincheng3");
        System.out.println(result3);


    }

}
