package com.gxc.rpc.server;

import com.gxc.rpc.server.config.RpcServerConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author GongXincheng
 * @date 2019-12-10 20:59
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RpcServerConfig.class)
public class RpcServerTestWithSpring {

    @Test
    public void test01() throws InterruptedException {
        // spring会自动注册服务，只要保证容器存活即可
        Thread.sleep(Integer.MAX_VALUE);
    }

}
