package com.gxc.provider.user.service;

import com.gxc.rpc.common.entity.ProductReq;
import com.gxc.rpc.common.service.ProductService;
import com.gxc.rpc.core.annotation.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author GongXincheng
 * @date 2020-03-02 00:11
 */
@Service(value = ProductService.class)
public class ProductServiceImpl implements ProductService {

    @Override
    public Map<String, Object> getProduct(ProductReq req) {
        System.out.println("入参：" + req);

        Map<String, Object> map = new HashMap<>(16);
        for (int i = 0; i < 12; i++) {
            ProductReq product = new ProductReq(i, "product_" + i, new BigDecimal(i + ""), LocalDateTime.now());
            map.put(i + "", product);
        }
        return map;
    }

}
