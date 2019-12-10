package com.gxc.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author chusen
 * @date 2019/12/9 17:03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse implements Serializable {
    private Integer code;
    private String msg;
    private Object data;
}