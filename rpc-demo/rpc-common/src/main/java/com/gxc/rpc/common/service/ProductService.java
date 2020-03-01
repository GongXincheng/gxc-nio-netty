package com.gxc.rpc.common.service;

import com.gxc.rpc.common.entity.ProductReq;

import java.util.Map;

/**
 * @author GongXincheng
 * @date 2020-03-02 00:08
 */
public interface ProductService {

    Map<String, Object> getProduct(ProductReq req);

}
