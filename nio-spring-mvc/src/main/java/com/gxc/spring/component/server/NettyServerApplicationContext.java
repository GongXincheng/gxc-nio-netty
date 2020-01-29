package com.gxc.spring.component.server;

import com.gxc.spring.component.context.AbstractApplicationContext;

/**
 * @author GongXincheng
 * @date 2020-01-13 09:35
 */
public class NettyServerApplicationContext extends AbstractApplicationContext {
    /**
     * 构造启动.
     *
     * @param config 配置类
     */
    public NettyServerApplicationContext(Class<?> config) {
        super(config);
    }

    @Override
    public void start() throws Exception {

    }


    @Override
    public void close() throws Exception {
        super.close();
    }
}
