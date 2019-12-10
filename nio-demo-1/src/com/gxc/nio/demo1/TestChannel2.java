package com.gxc.nio.demo1;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author GongXincheng
 * @date 2019-12-08 14:18
 */
public class TestChannel2 {


    /**
     * 3.2：读取文件中内容
     */
    @Test
    public void testUseChannelToCopyFile31() throws Exception {
        FileChannel inChannel = FileChannel.open(Paths.get("1.txt"), StandardOpenOption.READ);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while(inChannel.read(buffer) != -1) {
            buffer.flip();
            System.out.println(new String(buffer.array(), 0, buffer.limit()));
            buffer.clear();
        }
        inChannel.close();
    }


    /**
     * 3.1：通道之间的数据传输（直接缓冲区）
     */
    @Test
    public void testUseChannelToCopyFile3() throws Exception {
        FileChannel inChannel = FileChannel.open(Paths.get("/Users/gxc/tmp/1.png"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("/Users/gxc/tmp/head_2_1.png"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ);

        inChannel.transferTo(0, inChannel.size(), outChannel);

        outChannel.close();
        inChannel.close();
    }

    /**
     * 2： 使用直接缓冲区完成文件的复制（内存映射文件）
     */
    @Test
    public void testUseChannelToCopyFile2() throws Exception {
//        FileInputStream fis = new FileInputStream("/Users/gxc/tmp/1.png");
//        FileOutputStream fos = new FileOutputStream("/Users/gxc/tmp/head_2_1.png");

        // 方式1，获取Channel
//        RandomAccessFile fis = new RandomAccessFile("/Users/gxc/tmp/1.png", "rw");
//        RandomAccessFile fos = new RandomAccessFile("/Users/gxc/tmp/head_2_1.png", "rw");
//        FileChannel inChannel = fis.getChannel();
//        FileChannel outChannel = fos.getChannel();

        // 方式2：获取channel
        FileChannel inChannel = FileChannel.open(Paths.get("/Users/gxc/tmp/1.png"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("/Users/gxc/tmp/head_2_1.png"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ);

        MappedByteBuffer inBuffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        MappedByteBuffer outBuffer = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());

        byte[] dst = new byte[inBuffer.limit()];
        inBuffer.get(dst);
        outBuffer.put(dst);

        // 关闭资源
        outChannel.close();
        inChannel.close();
//        fos.close();
//        fis.close();

    }

    /**
     * 1：利用通道完成 文件的复制（非直接缓冲区）
     */
    @Test
    public void testUseChannelToCopyFile() throws Exception {
        FileInputStream fis = new FileInputStream("/Users/gxc/tmp/1.png");
        FileOutputStream fos = new FileOutputStream("/Users/gxc/tmp/head_2_1.png");

        // 获取Channel
        FileChannel inChannel = fis.getChannel();
        FileChannel outChannel = fos.getChannel();

        // 创建缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(2048);

        // 循环读写，其中inChannel.read() 在Buffer中为写模式，将文件内容写入到buffer中
        while (inChannel.read(buffer) != -1) {
            // 将写模式 切换为读模式
            buffer.flip();

            // 将缓冲区写入到 outChannel 中
            outChannel.write(buffer);

            // 清除缓冲区，继续循环读写
            buffer.clear();
        }

        // 关闭资源
        outChannel.close();
        inChannel.close();
        fos.close();
        fis.close();
    }


}
