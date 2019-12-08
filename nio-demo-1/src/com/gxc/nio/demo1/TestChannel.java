package com.gxc.nio.demo1;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.SortedMap;

/**
 * Channel 测试类
 * <p>
 * 用于源节点与目标节点的连接，在 Java NIO 中负责缓冲区中的数据传输。
 * Channel 本身不存储数据，因此需要配合 Buffer 进行传输
 *
 * @author GongXincheng
 * @date 2019-12-07 18:39
 */
public class TestChannel {

    /**
     * 5：字符集
     */
    @Test
    public void test() throws CharacterCodingException {
        Charset cs1 = StandardCharsets.UTF_8;

        // 获取编码器和解码器
        CharsetEncoder ce = cs1.newEncoder();
        CharsetDecoder cd = cs1.newDecoder();

        CharBuffer cb = CharBuffer.allocate(1024);
        cb.put("GongXincheng测试");
        cb.flip();

        // 编码
        ByteBuffer byteBuffer = ce.encode(cb);
        for (int i = 0; i < byteBuffer.limit(); i++) {
            System.out.println(byteBuffer.get());
        }

        byteBuffer.flip();

        // 解码
        System.out.println("------------");
        CharBuffer charBuffer = cd.decode(byteBuffer);
        System.out.println(charBuffer.toString());
    }


    /**
     * 4：分散聚集
     */
    @Test
    public void testUseChannelToCopyFile4() throws Exception {
        RandomAccessFile raf = new RandomAccessFile("1.txt", "rw");

        // 1：获取通道
        FileChannel channel = raf.getChannel();

        // 2：分配指定大小的缓冲区
        ByteBuffer buf1 = ByteBuffer.allocate(100);
        ByteBuffer buf2 = ByteBuffer.allocate(1024);

        // 3：分散读取
        ByteBuffer[] bufs = {buf1, buf2};
        channel.read(bufs);

        for (ByteBuffer buf : bufs) {
            buf.flip();
        }
        System.out.println(new String(bufs[0].array(), 0, bufs[0].limit()));
        System.out.println("----------------------");
        System.out.println(new String(bufs[1].array(), 0, bufs[1].limit()));

        // 4：聚集写入
        RandomAccessFile raf2 = new RandomAccessFile("1-1.txt", "rw");
        FileChannel channel2 = raf2.getChannel();

        channel2.write(bufs);
    }

    /**
     * 3：通道之间的数据传输（直接缓冲区）
     */
    @Test
    public void testUseChannelToCopyFile3() throws Exception {
        FileChannel inChannel = FileChannel.open(
                Paths.get("/Users/gxc/tmp/sl.mp4"),
                StandardOpenOption.READ);

        FileChannel outChannel = FileChannel.open(
                Paths.get("/Users/gxc/tmp/sl5.mp4"),
                StandardOpenOption.READ,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE);

        //inChannel.transferTo(0, inChannel.size(), outChannel);
        outChannel.transferFrom(inChannel, 0, inChannel.size());

        inChannel.close();
        outChannel.close();
    }

    /**
     * 2： 使用直接缓冲区完成文件的复制（内存映射文件）
     */
    @Test
    public void testUseChannelToCopyFile2() throws Exception {
        FileChannel inChannel = FileChannel.open(
                Paths.get("/Users/gxc/tmp/sl.mp4"),
                StandardOpenOption.READ);

        FileChannel outChannel = FileChannel.open(
                Paths.get("/Users/gxc/tmp/sl3.mp4"),
                StandardOpenOption.READ,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE);

        // 内存映射文件
        MappedByteBuffer inMappedBuf = inChannel.map(
                FileChannel.MapMode.READ_ONLY, 0, inChannel.size());

        MappedByteBuffer outMappedBuf = outChannel.map(
                FileChannel.MapMode.READ_WRITE, 0, inChannel.size());

        // 直接对缓冲区 进行数据的读写操作
        byte[] dst = new byte[inMappedBuf.limit()];
        inMappedBuf.get(dst);
        outMappedBuf.put(dst);

        inChannel.close();
        outChannel.close();
    }

    /**
     * 1：利用通道完成 文件的复制（非直接缓冲区）
     */
    @Test
    public void testUseChannelToCopyFile() throws Exception {
        FileInputStream fis = new FileInputStream("/Users/gxc/tmp/sl.mp4");
        FileOutputStream fos = new FileOutputStream("/Users/gxc/tmp/sl2.mp4");

        // 1：获取通道
        FileChannel inChannel = fis.getChannel();
        FileChannel outChannel = fos.getChannel();

        // 2：分配指定大小的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);

        // 3：将通道中的数据存入到缓冲区中
        while (inChannel.read(buf) != -1) {
            // 将 buffer 的写模式切换成读模式
            buf.flip();
            // 4：将缓冲区中的数据写入到通道中
            outChannel.write(buf);
            // 清除缓冲区
            buf.clear();
        }

        // 5：关闭资源
        outChannel.close();
        inChannel.close();
        fos.close();
        fis.close();
    }

}
