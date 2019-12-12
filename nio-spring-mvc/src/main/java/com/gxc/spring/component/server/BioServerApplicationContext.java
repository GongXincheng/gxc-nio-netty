package com.gxc.spring.component.server;

import com.gxc.spring.component.context.AbstractApplicationContext;

/**
 * @author GongXincheng
 * @date 2019-12-12 15:44
 */
public class BioServerApplicationContext extends AbstractApplicationContext {

    /**
     * 构造启动.
     *
     * @param config
     */
    public BioServerApplicationContext(Class<?> config) {
        super(config);
    }

    @Override
    public void start() {

    }

    @Override
    public void close() {

    }
}
