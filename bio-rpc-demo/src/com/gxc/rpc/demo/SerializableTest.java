package com.gxc.rpc.demo;


import it.chusen.socket.biorpc.BioRpcRequest;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

/**
 * @author GongXincheng
 * @date 2019-12-09 17:35
 */
public class SerializableTest {

    public static void main(String[] args) {
        BioRpcRequest request = new BioRpcRequest();
        request.setUsername("admin");

    }

}
