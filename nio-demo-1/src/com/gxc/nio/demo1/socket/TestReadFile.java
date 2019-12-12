package com.gxc.nio.demo1.socket;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author GongXincheng
 * @date 2019-12-11 23:41
 */
public class TestReadFile {

    public static void main(String[] args) throws Exception {

//        RandomAccessFile raf = new RandomAccessFile("/Users/gxc/project/git-project/gxc-nio-netty/nio-demo-1/1.txt",
        RandomAccessFile raf = new RandomAccessFile("1.txt",
                "rw");

        FileChannel channel = raf.getChannel();

        int size = (int) channel.size();
        if(size == 0) {
            channel.close();
            return;
        }
        ByteBuffer buffer = ByteBuffer.allocate(size);

        //读取内容到Buffer中
        channel.read(buffer);
        buffer.flip();
        System.out.println(new String(buffer.array(), 0, buffer.limit()));
        buffer.clear();

        // TODO：实现追加
        raf.seek(raf.length());
        FileChannel channel2 = raf.getChannel();

        buffer.put("admin".getBytes());
        buffer.flip();
        channel2.write(buffer);
        buffer.clear();

        channel.close();
        channel2.close();
        raf.close();
    }

}
