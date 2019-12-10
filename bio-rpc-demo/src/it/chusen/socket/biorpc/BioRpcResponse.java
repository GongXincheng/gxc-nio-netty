package it.chusen.socket.biorpc;

import java.io.Serializable;

/**
 * @author chusen
 * @date 2019/12/9 17:03
 */

public class BioRpcResponse implements Serializable {
    private Integer code;
    private String msg;
    private Object data;

    public BioRpcResponse() {
    }

    public BioRpcResponse(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static BioRpcResponse ok(String msg) {
        return new BioRpcResponse(200, msg, null);
    }

    public static BioRpcResponse fail(String msg) {
        return new BioRpcResponse(500, msg, null);
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
    public String toString() {
        return "BioRpcResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}