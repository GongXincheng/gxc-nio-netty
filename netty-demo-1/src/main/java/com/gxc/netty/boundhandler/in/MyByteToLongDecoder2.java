package com.gxc.netty.boundhandler.in;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Void：表示用户状态管理
 *
 * @author GongXincheng
 * @date 2020-02-11 17:55nnii
 */
@Slf4j
public class MyByteToLongDecoder2 extends ReplayingDecoder<Void> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        log.info("{} 的 decode 方法执行", this.getClass().getSimpleName());

        // 在 ReplayingDecoder 不需要判断是否有足够的数据，内部会进行处理判断
        out.add(in.readLong());

    }
}
