package com.gxc.netty.rpc.common;

/**
 * 接口（消费者和服务提供者公用）.
 *
 * @author GongXincheng
 * @date 2020-02-29 22:04
 */
public interface HelloService {

    /**
     * hello
     *
     * @param message message
     * @return String
     */
    String hello(String message);

}
