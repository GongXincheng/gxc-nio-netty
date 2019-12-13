package com.gxc.spring;

import com.gxc.spring.component.context.AnnotationConfigServerApplicationContext;
import com.gxc.spring.component.context.ApplicationContext;
import com.gxc.spring.config.GxcConfigurationBean;

/**
 * @author GongXincheng
 * @date 2019-12-12 10:02
 */
public class Main {

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new AnnotationConfigServerApplicationContext(GxcConfigurationBean.class);

//        UserController userController = context.getBean("userController", UserController.class);
//        User user = userController.findById(1);
//        System.out.println(user);

        //context.close();
    }

}
