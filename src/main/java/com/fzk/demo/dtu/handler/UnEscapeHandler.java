package com.fzk.demo.dtu.handler;

import com.fzk.demo.dtu.util.SDK;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UnEscapeHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof byte[]) {
            byte[] in = ((byte[]) msg);
            byte[] unEscapeBytes = SDK.unEscape(in);
            ctx.fireChannelRead(unEscapeBytes);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("UnEscapeHandler发生异常：", cause);
        ctx.close();
    }
}
