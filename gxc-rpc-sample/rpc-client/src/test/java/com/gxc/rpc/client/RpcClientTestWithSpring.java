package com.gxc.rpc.client;

import com.gxc.rpc.client.annotation.Reference;
import com.gxc.rpc.client.config.RpcClientConfig;
import com.gxc.rpc.interfaces.HelloService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author GongXincheng
 * @date 2019-12-10 21:42
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RpcClientConfig.class)
public class RpcClientTestWithSpring {

    /**
     * 自动注入，无需手动获取
     */
    @Reference
    private HelloService helloService;

    @Test
    public void test01() {

        String result = helloService.sayHello("gxc");
        System.out.println(result);

    }

}
