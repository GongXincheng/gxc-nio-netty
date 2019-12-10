package com.gxc.nio.demo1.file;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author GongXincheng
 * @date 2019-12-09 16:18
 */
public class FileCopyTest {

    public static void main(String[] args) throws Exception {

        FileChannel inChannel = FileChannel.open(Paths.get("/Users/gxc/tmp/sl.mp4"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("/Users/gxc/tmp/sl-2.mp4"), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        inChannel.transferTo(0, inChannel.size(), outChannel);

        outChannel.close();
        inChannel.close();
    }

}
