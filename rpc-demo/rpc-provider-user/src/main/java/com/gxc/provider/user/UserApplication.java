package com.gxc.provider.user;

import com.gxc.provider.user.config.UserConfiguration;
import com.gxc.spring.context.AnnotationConfigApplicationContext;

/**
 * @author GongXincheng
 * @date 2020-03-01 21:09
 */
public class UserApplication {

    public static void main(String[] args) throws Exception {
        new AnnotationConfigApplicationContext(UserConfiguration.class);
    }

}
