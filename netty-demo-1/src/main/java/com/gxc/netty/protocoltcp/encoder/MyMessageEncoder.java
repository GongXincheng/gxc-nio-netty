package com.gxc.netty.protocoltcp.encoder;

import com.gxc.netty.protocoltcp.MessageProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author GongXincheng
 * @date 2020-02-23 05:22
 */
@Slf4j
public class MyMessageEncoder extends MessageToByteEncoder<MessageProtocol> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageProtocol msg, ByteBuf out) throws Exception {
        log.info("[{}]的[encode()]方法被调用", this.getClass().getSimpleName());

        // TODO: 此处一定要注意！！编码器用什么样的顺序编码，解码器一定要用同样的顺序解码！！！
        out.writeInt(msg.getLen());
        out.writeBytes(msg.getContent());
    }
}
