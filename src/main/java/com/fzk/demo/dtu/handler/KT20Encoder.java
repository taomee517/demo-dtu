package com.fzk.demo.dtu.handler;

import com.fzk.dtu.utils.Secret2PlainUtil;
import com.fzk.sdk.util.BytesTranUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class KT20Encoder extends MessageToMessageEncoder {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List out) throws Exception {
        String downMsg = (String)msg;
        String finalDownMsg = null;
//        if (downMsg.length()>4) {
            finalDownMsg = Secret2PlainUtil.plain2Secret(downMsg);
//        }
        byte[] bytes = BytesTranUtil.hexStringToBytes(finalDownMsg);
        ByteBuf buf = Unpooled.buffer(bytes.length);
        buf.writeBytes(bytes);
        out.add(buf);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("KT20Encoder发生异常：", cause);
        ctx.close();
    }
}
