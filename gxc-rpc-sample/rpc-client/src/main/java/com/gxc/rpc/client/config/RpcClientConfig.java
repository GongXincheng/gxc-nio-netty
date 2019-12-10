
package com.gxc.rpc.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 客户端配置类
 *
 * @author GongXincheng
 * @date 2019-12-10 21:29
 */
@Configuration
public class RpcClientConfig {
    /**
     * 处理@Refrence注解标记的属性自动注入 * @return
     */
    @Bean
    public RpcProxyBeanPostProcessor serviceReferenceHandler() {
        return new RpcProxyBeanPostProcessor();
    }
}