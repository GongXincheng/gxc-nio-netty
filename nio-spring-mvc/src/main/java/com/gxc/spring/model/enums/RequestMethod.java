package com.gxc.spring.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author GongXincheng
 * @date 2019-12-12 13:51
 */
@Getter
@AllArgsConstructor
public enum RequestMethod {

    /**
     * 请求类型
     */
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE");

    private String code;

    public static RequestMethod codeOf(String code) {
        return Arrays.stream(RequestMethod.values())
                .filter(e -> Objects.equals(e.getCode(), code))
                .findFirst().orElseThrow(() -> new RuntimeException("未找到该请求类型"));
    }
}
