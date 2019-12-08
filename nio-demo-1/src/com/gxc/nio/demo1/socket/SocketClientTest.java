package com.gxc.nio.demo1.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author GongXincheng
 * @date 2019-12-08 15:52
 */
public class SocketClientTest {

    public static void main(String[] args) throws IOException, InterruptedException {

        Socket socket = new Socket("127.0.0.1", 9000);
        PrintWriter pw = new PrintWriter(socket.getOutputStream());
        BufferedReader br = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

        // 发送消息
        pw.println("Hello");
        pw.flush();
        socket.shutdownOutput();

        // 获取服务返回结果
        String line;
        while((line = br.readLine()) != null) {
            System.out.println(line);
        }
        socket.close();
    }

}
