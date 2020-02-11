package com.gxc.netty.boundhandler.in;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author GongXincheng
 * @date 2020-02-07 15:54
 */
@Slf4j
public class MyByteToLongDecoder extends ByteToMessageDecoder {

    /**
     * 解码：会根据接收到的数据，被调用多次，直到确定没有新的元素被添加到list
     *      或者是 ByteBuf 没有更多的可读字节为止
     *
     *  如果 List<Object> 不为空，就会将list的内容传递给下一个 ChannelHInboundHandler处理，
     *  该处理器的方法也会被调用多次！
     *
     * @param ctx 上下文对象
     * @param in  入站 ByteBuf
     * @param out List,将解码后的数据传给下一个 handler
     * @throws Exception 异常
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        log.info("{} 的 decode 方法执行", this.getClass().getSimpleName());

        // 因为 long 是8个字节，需要判断有8个字节，才能读取一个long
        if (in.readableBytes() >= 8) {
            out.add(in.readLong());
        }
    }
}
