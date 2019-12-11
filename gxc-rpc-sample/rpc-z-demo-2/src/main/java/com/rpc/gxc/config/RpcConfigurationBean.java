package com.rpc.gxc.config;

import com.rpc.gxc.GxcServer;
import com.rpc.gxc.registry.ServiceBeanRegistry;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author GongXincheng
 * @date 2019-12-11 10:44
 */
@Configuration
@ComponentScan(basePackages = "com.rpc.gxc.service")
public class RpcConfigurationBean {

    @Bean
    public BeanPostProcessor rpcProxyBeanPostProcessor() {
        return new RpcProxyBeanPostProcessor();
    }

    @Bean
    public ServiceBeanRegistry serviceBeanRegistry() {
        return new ServiceBeanRegistry();
    }

    @Bean
    public GxcServer gxcServer() {
        return new GxcServer(9000);
    }
}
