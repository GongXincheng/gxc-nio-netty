package com.gxc.nio.demo3;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author GongXincheng
 * @date 2020-02-24 11:58
 */
public class ReadStringFileDemo {

    public static void main(String[] args) throws Exception {

//        FileInputStream inputStream = new FileInputStream(new File("/Users/gxc/project/git-project/gxc-nio-netty/nio-demo-1/1.txt"));
//        FileChannel fileChannel = inputStream.getChannel();

        FileChannel fileChannel = FileChannel.open(Paths.get("/Users/gxc/project/git-project/gxc-nio-netty/nio-demo-1/1.txt"), StandardOpenOption.READ);

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        StringBuilder sb = new StringBuilder();
        while (fileChannel.read(buffer) != -1) {
            buffer.flip();
            sb.append(new String(buffer.array(), 0, buffer.limit(), StandardCharsets.UTF_8));
            buffer.clear();
        }

        System.out.println("内容：\n" + sb.toString());

        fileChannel.close();
//        inputStream.close();
    }

    @Test
    public void test() throws Exception {
        FileChannel fileChannel = FileChannel.open(Paths.get("1.txt"), StandardOpenOption.READ);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (fileChannel.read(buffer) != -1) {
            buffer.flip();
            System.out.print(new String(buffer.array(), 0, buffer.limit(), StandardCharsets.UTF_8));
            buffer.clear();
        }
        fileChannel.close();
    }

}
