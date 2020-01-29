package com.gxc.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

/**
 * @author GongXincheng
 * @date 2020-01-29 17:45
 */
public class NettyByteBuf_01 {

    public static void main(String[] args) {

        // 创建一个ByteBuf
        // 1：创建对象：包含一个数组arr, 是一个byte[10]
        // 2：Netty的buffer中不需要使用flip进行读写切换
        //   底层维护了 readerIndex & writerIndex
        //
        ByteBuf buffer = Unpooled.buffer(10);

        // 写
        for (int i = 0; i < 10; i++) {
            buffer.writeByte(i);
        }

        System.out.println("=======");

        // 输出
        for (int i = 0; i < buffer.capacity(); i++) {
            System.out.println(buffer.readByte());
        }

        buffer.clear();

        byte[] array = buffer.array();
        System.out.println(new String(array, StandardCharsets.UTF_8));

        System.out.println(buffer);

    }

}
