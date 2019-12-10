package it.chusen.socket.biorpc;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author chusen
 * @date 2019/12/9 17:03
 */
public class MyServer {


    static class HandleMsg extends Thread {
        private Socket socket;
        HandleMsg(Socket socket) {
            this.socket = socket;
        }
        @Override
        public void run() {
            try {
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int length = 0;
                while ((length = inputStream.read(buf)) != -1) {
                    byteArrayOutputStream.write(buf, 0, length);
                }
                BioRpcRequest request = SerializeUtil.getRequest(byteArrayOutputStream.toByteArray());

                String className = request.getClassName();
                if ("UserService".equals(className)) {
                    String methodName = request.getMethodName();
                    if ("login".equals(methodName)) {
                        BioRpcResponse response = new UserService().login(request.getUsername(), request.getPassword());
                        byte[] bytes = SerializeUtil.parseToResponse(response);
                        outputStream.write(bytes);
                    } else {
                        BioRpcResponse response = BioRpcResponse.fail("方法名错误！");
                        outputStream.write(SerializeUtil.parseToResponse(response));
                    }
                } else {
                    // 写出去
                    BioRpcResponse response = BioRpcResponse.fail("类名错误！");
                    outputStream.write(SerializeUtil.parseToResponse(response));
                }

            } catch (Exception e) {

            }
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(10000);
        System.out.println("服务端启动成功...");
        while (true) {
            Socket socket = serverSocket.accept();
            new HandleMsg(socket).start();
        }
    }
}
