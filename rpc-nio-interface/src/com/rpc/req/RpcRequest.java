package com.gxc.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author GongXincheng
 * @date 2019-12-09 17:06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements Serializable {

    private String className;
    private String methodName;
    private String username;
    private String password;

}
