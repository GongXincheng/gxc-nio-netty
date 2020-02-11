package com.gxc.netty.boundhandler.out;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author GongXincheng
 * @date 2020-02-07 16:27
 */
@Slf4j
public class MyLongToByteEncoder extends MessageToByteEncoder<Long> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Long msg, ByteBuf out) throws Exception {
        log.info("MyLongToByteEncoder 的 encode 方法被调用，msg：[{}]", msg);
        out.writeLong(msg);
    }
}
