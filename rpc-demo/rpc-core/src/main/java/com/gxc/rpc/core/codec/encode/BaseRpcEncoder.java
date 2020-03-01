package com.gxc.rpc.core.codec.encode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

/**
 * @author GongXincheng
 * @date 2020-03-01 22:04
 */
public abstract class BaseRpcEncoder<T> extends MessageToByteEncoder<T> {

    @Override
    protected void encode(ChannelHandlerContext ctx, T msg, ByteBuf out) throws Exception {
        // 将 RpcRequest 对象转为字节数组
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(msg);
        byte[] bytes = baos.toByteArray();
        int length = bytes.length;

        // 发送
        out.writeInt(length);
        out.writeBytes(bytes);
    }
}
