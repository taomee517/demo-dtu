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
        try {
            byte[] data = SDK.split(in.retain());
            if (Objects.nonNull(data)) {
                out.add(data);
            }else {
                log.info("拆包结果为空！");
            }
        } finally {
            resetBuffer(ctx,in);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("FrameSplitHandler发生异常：", cause);
        ctx.close();
    }



    /**
     * 移动指针到开始位置
     *
     * @param ctx
     * @param in
     */
    private void resetBuffer(ChannelHandlerContext ctx, ByteBuf in) {
        int left = in.readableBytes();
        int start = in.readerIndex();
        if (left > 0 && in.readerIndex() > 0) {
            for (int index = 0; index < left; index++) {
                in.setByte(index, in.getByte(index + start));
            }
            in.setIndex(0, left);
        }
    }
}
