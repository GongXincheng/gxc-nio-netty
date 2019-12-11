package com.rpc.gxc;

import com.gxc.rpc.interfaces.HelloService;
import com.rpc.gxc.annnotation.Reference;
import com.rpc.gxc.config.RpcConfigurationBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * @author GongXincheng
 * @date 2019-12-11 10:35
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RpcConfigurationBean.class)
public class GxcServerTest {

    @Reference
    private HelloService helloService;

    @Test
    public void test() {
        String gongXincheng = helloService.sayHello("GongXincheng");
        System.out.println(gongXincheng);
    }



}