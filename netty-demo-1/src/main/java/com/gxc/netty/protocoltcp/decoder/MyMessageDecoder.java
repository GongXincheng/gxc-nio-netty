package com.gxc.netty.protocoltcp.decoder;

import com.gxc.netty.protocoltcp.MessageProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author GongXincheng
 * @date 2020-02-23 05:37
 */
@Slf4j
public class MyMessageDecoder extends ReplayingDecoder<Void> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        log.info("[{}]的[decode()]方法被调用", this.getClass().getSimpleName());

        // TODO: 此处一定要注意！！编码器用什么样的顺序编码，解码器一定要用同样的顺序解码！！！
        // 将得到的二进制字节码 转成 MessageProtocol 数据包(对象)
        int length = in.readInt();


        byte[] bytes = new byte[length];
        in.readBytes(bytes);

        // 封装成 MessageProtocol 对象 放入out，传递下一个handler中
        MessageProtocol messageProtocol = MessageProtocol.builder()
                .len(length)
                .content(bytes).build();

        out.add(messageProtocol);
    }
}
