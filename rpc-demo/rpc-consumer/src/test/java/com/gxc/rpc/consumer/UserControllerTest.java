package com.gxc.rpc.consumer;

import com.gxc.rpc.common.entity.UserEntity;
import com.gxc.rpc.consumer.config.ConsumerConfiguration;
import com.gxc.rpc.consumer.controller.UserController;
import com.gxc.spring.context.AnnotationConfigApplicationContext;
import com.gxc.spring.context.ApplicationContext;
import org.junit.Test;

/**
 * @author GongXincheng
 * @date 2020-03-01 16:46
 */
public class UserControllerTest {

    @Test
    public void hello() throws Exception {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(ConsumerConfiguration.class);
        UserController userController = (UserController) applicationContext.getBean("userController");

        for (int i = 0; i < 10; i++) {
            String result = userController.hello("GongXincheng" + i);
            System.out.println(result);
        }

    }

    @Test
    public void findByCondition() throws Exception {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(ConsumerConfiguration.class);
        UserController userController = (UserController) applicationContext.getBean("userController");

        UserEntity admin = userController.findByCondition(1, "admin");
        System.out.println(admin);
    }
}