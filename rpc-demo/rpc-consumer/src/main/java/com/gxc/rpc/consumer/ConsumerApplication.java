package com.gxc.rpc.consumer;

import com.gxc.rpc.common.entity.UserEntity;
import com.gxc.rpc.consumer.config.ConsumerConfiguration;
import com.gxc.rpc.consumer.controller.ProductController;
import com.gxc.rpc.consumer.controller.UserController;
import com.gxc.spring.context.AnnotationConfigApplicationContext;
import com.gxc.spring.context.ApplicationContext;

/**
 * @author GongXincheng
 * @date 2020-03-02 00:01
 */
public class ConsumerApplication {

    public static void main(String[] args) throws Exception {

        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(ConsumerConfiguration.class);
        UserController userController = (UserController) applicationContext.getBean("userController");
        ProductController productController = (ProductController) applicationContext.getBean("productController");

//        for (int i = 0; i < 10; i++) {
////            String result = userController.hello("GongXincheng" + i);
////            System.out.println(result);
//
//            UserEntity userEntity = userController.findByCondition(i, "Admin_" + i);
//            System.out.println(userEntity);
//        }

        productController.test();

    }

}
