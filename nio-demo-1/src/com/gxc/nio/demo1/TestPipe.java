package com.gxc.nio.demo1;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

/**
 * Pipe管道
 * @author GongXincheng
 * @date 2019-12-08 23:58
 */
public class TestPipe {

    @Test
    public void testPipe() throws Exception {
        // 1：获取管道
        Pipe pipe = Pipe.open();

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put("通过单向管道发送数据".getBytes());
        buffer.flip();

        // 2：将缓冲区的数据接入管道
        Pipe.SinkChannel sinkChannel = pipe.sink();
        sinkChannel.write(buffer);

        // 3：读取缓冲区中的数据
        buffer.flip();
        Pipe.SourceChannel sourceChannel = pipe.source();
        sourceChannel.read(buffer);
        System.out.println(new String(buffer.array(), 0, buffer.limit()));
    }

}
