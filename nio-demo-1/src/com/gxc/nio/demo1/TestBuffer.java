package com.gxc.nio.demo1;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * 测试Buffer.
 * <p>
 * position <= limit <= capacity
 *
 * @author GongXincheng
 * @date 2019-12-07 16:25
 */
public class TestBuffer {

    @Test
    public void test1(){
        String str = "abcde";
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        /**
         * 为Buffer分配空间(数组长度)
         */
        System.out.println("/////////////// allocate() ////////////////");
        // 1024
        System.out.println(buffer.capacity());
        // 1024
        System.out.println(buffer.limit());
        // 0
        System.out.println(buffer.position());

        System.out.println("/////////////// put() ///////////////");
        buffer.put(str.getBytes(StandardCharsets.UTF_8));
        // 1024
        System.out.println(buffer.capacity());
        // 1024
        System.out.println(buffer.limit());
        // 5
        System.out.println(buffer.position());

        /**
         * flip()：将写模式变成读模式，limit = position，position = 0
         */
        System.out.println("/////////////// flip()  ///////////////");
        buffer.flip();
        // 1024
        System.out.println(buffer.capacity());
        // 5
        System.out.println(buffer.limit());
        // 0
        System.out.println(buffer.position());


        /**
         * 利用get()读取缓冲区的数据
         */
        System.out.println("/////////////// get()  ///////////////");
        printBuffer(buffer, 5);
        // 1024
        System.out.println(buffer.capacity());
        // 5
        System.out.println(buffer.limit());
        // 5
        System.out.println(buffer.position());

        /**
         * rewind() 可重复读数据：position = 0
         */
        System.out.println("/////////////// rewind()  ///////////////");
        buffer.rewind();
        // 1024
        System.out.println(buffer.capacity());
        // 5
        System.out.println(buffer.limit());
        // 0
        System.out.println(buffer.position());
        printBuffer(buffer, buffer.limit());

        /**
         * clear() 清空缓冲区，但是缓冲区中的数据都还在，处于"被遗忘"状态：
         *      position = 0, limit = capacity
         */
        System.out.println("/////////////// clear()  ///////////////");
        buffer.clear();
        // 1024
        System.out.println(buffer.capacity());
        // 1024
        System.out.println(buffer.limit());
        // 0
        System.out.println(buffer.position());
        printBuffer(buffer, 5);

    }

    /**
     * 读取 Buffer 中的数据
     *
     * @param buffer   ByteBuffer
     * @param byteSize 初始化 byte数组长度
     */
    private static void printBuffer(ByteBuffer buffer, int byteSize) {
        byte[] bytes = new byte[byteSize];
        buffer.get(bytes);
        // abcde
        System.out.println(new String(bytes, 0, bytes.length));
    }

    /**
     * 用来测试 mark 标记
     */
    @Test
    public void test2() {
        String str = "abcde";

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put(str.getBytes());

        buffer.flip();

        byte[] dst = new byte[buffer.limit()];
        buffer.get(dst, 0, 2);
        // ab
        System.out.println(new String(dst, 0, 2));

        // 1024
        System.out.println(buffer.capacity());
        // 5
        System.out.println(buffer.limit());
        // 2
        System.out.println(buffer.position());

        /**
         * mark()：标记position的位置
         */
        System.out.println("///////////// mark() /////////////");
        buffer.mark();
        buffer.get(dst, 2, 2);
        // cd
        System.out.println(new String(dst, 2, 2));
        // 4
        System.out.println(buffer.position());

        /**
         * reset()：position 恢复到mark的位置
         */
        System.out.println("///////////// reset() /////////////");
        buffer.reset();
        // 2
        System.out.println(buffer.position());
        if (buffer.hasRemaining()) {
            int remaining = buffer.remaining();
            buffer.get(dst, 2, remaining);
            System.out.println(remaining);
            System.out.println(new String(dst, 2, 3));
        }
    }

}
