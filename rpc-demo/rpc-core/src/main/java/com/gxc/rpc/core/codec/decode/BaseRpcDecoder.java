package com.gxc.rpc.core.codec.decode;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.ReplayingDecoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * @author GongXincheng
 * @date 2020-03-01 21:58
 */
abstract class BaseRpcDecoder extends ReplayingDecoder<Void> {

    Object byteArrayToObject(ByteBuf in) throws IOException, ClassNotFoundException {
        int length = in.readInt();
        byte[] content = new byte[length];
        in.readBytes(content);

        // 将字节数组转成 对象
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(content));
        return objectInputStream.readObject();
    }

}
