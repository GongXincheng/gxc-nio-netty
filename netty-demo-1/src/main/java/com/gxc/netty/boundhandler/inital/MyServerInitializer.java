package com.gxc.netty.boundhandler.inital;

import com.gxc.netty.boundhandler.handler.MyServerHandler;
import com.gxc.netty.boundhandler.in.MyByteToLongDecoder;
import com.gxc.netty.boundhandler.out.MyLongToByteEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @author GongXincheng
 * @date 2020-02-07 15:49
 */
public class MyServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // 「入站」Handler进行【解码】 MyByteToLongDecoder;
        pipeline.addLast(new MyByteToLongDecoder());

        // 「出站」Handler进行【编码】 MyLongToByteEncoder
        pipeline.addLast(new MyLongToByteEncoder());

        pipeline.addLast(new MyServerHandler());

    }
}
