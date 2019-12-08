package com.gxc.nio.demo1.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author GongXincheng
 * @date 2019-12-08 15:45
 */
public class SocketServerTest {

    public static void main(String[] args) throws Exception {

        ServerSocket server = new ServerSocket(9000);
        Socket accept = server.accept();

        BufferedReader br = new BufferedReader(new InputStreamReader(accept.getInputStream()));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(accept.getOutputStream()));

        String line;
        while((line = br.readLine()) != null) {
            System.out.println(line);
        }

        bw.write("adminasdadadsad");
        bw.flush();

        accept.close();
        server.close();
    }

}
