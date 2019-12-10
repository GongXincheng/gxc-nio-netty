package com.gxc.rpc;

import com.gxc.rpc.util.SerializeUtil;
import it.chusen.socket.biorpc.BioRpcRequest;
import it.chusen.socket.biorpc.BioRpcResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author GongXincheng
 * @date 2019-12-09 17:03
 */
public class MyClient {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // 获取用户名
        BioRpcRequest req = getUserNameAndPassword(sc);

        Socket socket = null;
        ObjectInputStream ois = null;
        try {
            while (true) {
                // 连接服务端
                socket = new Socket("192.168.11.23", 10000);
                // 调用
                process(socket, req);
                // 输入完成
                socket.shutdownOutput();

                // 获取返回结果
                ois = new ObjectInputStream(socket.getInputStream());
                BioRpcResponse bioRpcResponse = getResponse(ois);

                System.out.println(bioRpcResponse);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sc.close();
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 获取返回结果
     *
     * @param ois ObjectInputStream
     */
    private static BioRpcResponse getResponse(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        return (BioRpcResponse) ois.readObject();
    }

    /**
     * 调用
     *
     * @param socket socket
     * @param req    req
     */
    private static void process(Socket socket, BioRpcRequest req) throws IOException {
        byte[] bytes = SerializeUtil.requestToByteArray(req);
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(bytes);
    }

    private static BioRpcRequest getUserNameAndPassword(Scanner sc) {
//        System.out.print("Username：");
//        String username = sc.next();
//        System.out.print("Password：");
//        String password = sc.next();
        BioRpcRequest result = new BioRpcRequest();
        result.setUsername("chusen");
        result.setPassword("123");
        result.setClassName("UserService");
        result.setMethodName("login");
        return result;
    }

}
