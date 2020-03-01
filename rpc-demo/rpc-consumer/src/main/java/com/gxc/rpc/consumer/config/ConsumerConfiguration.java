package com.gxc.rpc.consumer.config;

import com.gxc.rpc.consumer.controller.ProductController;
import com.gxc.rpc.consumer.controller.UserController;
import com.gxc.rpc.consumer.netty.NettyClient;
import com.gxc.spring.annotation.Bean;
import com.gxc.spring.annotation.Configuration;
import com.gxc.spring.context.ContainerRun;
import com.gxc.spring.lifecycle.BeanPostProcessor;

/**
 * @author GongXincheng
 * @date 2020-03-01 16:40
 */
@Configuration
public class ConsumerConfiguration implements ContainerRun {

    @Bean
    public BeanPostProcessor beanPostProcessor() {
        return new RpcProxyBeanPostProcessor();
    }

    @Bean
    public UserController userController() {
        return new UserController();
    }

    @Bean
    public ProductController productController() {
        return new ProductController();
    }


    @Override
    public void start() {
        NettyClient.startClient("127.0.0.1", 8899);
    }
}
