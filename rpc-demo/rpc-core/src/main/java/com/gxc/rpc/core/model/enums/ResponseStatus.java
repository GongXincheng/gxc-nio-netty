package com.gxc.rpc.core.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应状态
 *
 * @author GongXincheng
 * @date 2020-03-01 02:49
 */
@Getter
@AllArgsConstructor
public enum ResponseStatus {

    /**
     * 响应状态.
     */
    SUCCESS(1, "成功"),
    FAILED(0, "失败");

    private int state;
    private String desc;

}
