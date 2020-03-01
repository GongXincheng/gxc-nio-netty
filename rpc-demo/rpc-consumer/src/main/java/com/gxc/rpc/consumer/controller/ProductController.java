package com.gxc.rpc.consumer.controller;

import com.gxc.rpc.common.entity.ProductReq;
import com.gxc.rpc.common.service.ProductService;
import com.gxc.rpc.core.annotation.Reference;

import java.util.Map;

/**
 * @author GongXincheng
 * @date 2020-03-02 00:15
 */
public class ProductController {

    @Reference
    private ProductService productService;

    public void test() {
        ProductReq productReq = new ProductReq();
        productReq.setId(100000);
        Map<String, Object> product = productService.getProduct(productReq);

        for (Map.Entry<String, Object> entry : product.entrySet()) {
            System.out.println(entry.getKey() + "：：" + entry.getValue());
        }
    }

}
