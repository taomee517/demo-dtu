package com.fzk.demo.dtu.handler;

import com.fzk.demo.dtu.util.KT20CodecUtil;
import com.fzk.dtu.utils.Secret2PlainUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Slf4j
public class KT20Decoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        String str = KT20CodecUtil.Byte2StringSerialize(in);
        if (StringUtils.isNotEmpty(str)) {
            str = Secret2PlainUtil.secret2Plain(str);
            out.add(str.toUpperCase());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("KT20Decoder发生异常：", cause);
        ctx.close();
    }
}
