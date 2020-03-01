package com.gxc.rpc.core.protocol;

import com.gxc.rpc.core.model.enums.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author GongXincheng
 * @date 2020-03-01 02:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse<T> implements Serializable {

    private static final long serialVersionUID = 2L;

    /**
     * 响应状态 0失败，1成功
     */
    private int status;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 返回结果数据
     */
    private T data;

    public static <T> RpcResponse<T> success() {
        return new RpcResponse<T>(ResponseStatus.SUCCESS.getState(), "", null);
    }

    public static <T> RpcResponse<T> success(T data) {
        return new RpcResponse<T>(ResponseStatus.SUCCESS.getState(), "", data);
    }

    public static <T> RpcResponse<T> error(String errorMsg) {
        return new RpcResponse<T>(ResponseStatus.FAILED.getState(), errorMsg, null);
    }

}
