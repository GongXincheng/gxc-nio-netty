package com.gxc.spring.component.model;

import lombok.Data;

import java.util.Map;

/**
 * 存放请求头和请求体
 *
 * @author GongXincheng
 * @date 2019-12-13 10:42
 */
@Data
public class RequestDTO {

    private String method;
    private String uri;
    private String protocol;
    Map<String, String> headers;
    private String body;

}
