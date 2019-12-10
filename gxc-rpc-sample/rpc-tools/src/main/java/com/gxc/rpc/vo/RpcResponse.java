package com.gxc.rpc.vo;

import java.io.Serializable;

/**
 * @author GongXincheng
 * @date 2019-12-10 16:13
 */
public class RpcResponse implements Serializable {

    private static final long serialVersionUID = 2L;

    /**
     * 响应状态 0失败，1成功
     */
    private int status;

    /**
     * 错误信息
     */
    private String error;

    /**
     * 返回结果数据
     */
    private Object data;


    public RpcResponse() {
    }

    public RpcResponse(int status, String error, Object data) {
        this.status = status;
        this.error = error;
        this.data = data;
    }

    public static RpcResponse ok(Object data) {
        return build(1, null, data);
    }

    public static RpcResponse error(String error) {
        return build(0, error, null);
    }

    public static RpcResponse build(int status, String error, Object data) {
        RpcResponse r = new RpcResponse();
        r.setStatus(status);
        r.setError(error);
        r.setData(data);
        return r;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
