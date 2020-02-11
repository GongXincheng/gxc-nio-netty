package com.gxc.netty.boundhandler.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author GongXincheng
 * @date 2020-02-07 16:26
 */
@Slf4j
public class MyClientHandler extends SimpleChannelInboundHandler<Long> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {
        log.info("MyClientHandler 得到服务端返回的Long：[{}]", msg);
    }

    /**
     * 发送数据
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("MyClientHandler 发送数据");

        // 发送的是一个Long
        ctx.writeAndFlush(123456L);

        // 1："abcdabcdabcdabcd" 是16个字节

        // 2：该处理器的前一个Handler 是 MyLongToByteEncoder

        // 3：MyLongToByteEncoder 父类 MessageToByteEncoder 有一个 write()中的 acceptOutboundMessage() 方法
        // 判断当前数据是不是应该处理的类型，如果是则编码，否则跳过编码

        // 4：因此我们编写 Encoder 时要注意传入的数据类型 和 处理的数据类型一致
        //ctx.writeAndFlush(Unpooled.copiedBuffer("abcdabcdabcdabcd", CharsetUtil.UTF_8));
    }
}
