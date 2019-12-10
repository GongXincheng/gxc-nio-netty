package com.gxc.rpc.req;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author GongXincheng
 * @date 2019-12-09 17:06
 */
public class BioRpcRequest implements Serializable {



    private String className;
    private String methodName;
    private String username;
    private String password;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BioRpcRequest that = (BioRpcRequest) o;
        return className.equals(that.className) &&
                methodName.equals(that.methodName) &&
                username.equals(that.username) &&
                password.equals(that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, methodName, username, password);
    }

    @Override
    public String toString() {
        return "BioRpcRequest{" +
                "className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
