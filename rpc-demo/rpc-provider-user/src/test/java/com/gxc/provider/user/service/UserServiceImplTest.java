package com.gxc.provider.user.service;

import com.gxc.provider.user.config.UserConfiguration;
import com.gxc.spring.context.AnnotationConfigApplicationContext;
import com.gxc.spring.context.ApplicationContext;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author GongXincheng
 * @date 2020-03-01 03:46
 */
public class UserServiceImplTest {

    @Test
    public void hello() throws Exception {

        ApplicationContext context = new AnnotationConfigApplicationContext(UserConfiguration.class);

        Object bean = context.getBean("1");
        System.out.println(bean);
    }

    @Test
    public void findByCondition() {
    }
}