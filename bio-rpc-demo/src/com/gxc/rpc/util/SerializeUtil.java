package it.chusen.socket.biorpc;

import java.io.*;

/**
 * @author chusen
 * @date 2019/12/9 17:18
 */
public class SerializeUtil {
    public static BioRpcRequest getRequest(byte[] buf) {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(buf));
            return (BioRpcRequest) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("类转换失败！");
    }

    public static byte[] parseToResponse(BioRpcResponse bioRpcResponse) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(bioRpcResponse);
        return byteArrayOutputStream.toByteArray();
    }
}