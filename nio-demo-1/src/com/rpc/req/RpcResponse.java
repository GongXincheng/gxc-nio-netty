package com.rpc.req;


import java.io.Serializable;
import java.util.Objects;

/**
 * @author chusen
 * @date 2019/12/9 17:03
 */
public class RpcResponse implements Serializable {
    private Integer code;
    private String msg;
    private Object data;

    public RpcResponse() {
    }

    public RpcResponse(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RpcResponse that = (RpcResponse) o;
        return code.equals(that.code) &&
                msg.equals(that.msg) &&
                data.equals(that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, msg, data);
    }

    @Override
    public String toString() {
        return "RpcResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}