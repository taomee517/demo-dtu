package com.fzk.demo.dtu.handler;

import com.fzk.demo.dtu.util.SDK;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

@Slf4j
public class FrameSplitHandler extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        ByteBuf buf = SDK.split(in);
        if (Objects.nonNull(buf)) {
            out.add(buf);
        }else {
            log.info("拆包发生异常，结果为空！");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("FrameSplitHandler发生异常：", cause);
        ctx.close();
    }
}
