package com.fzk.demo.dtu.util;

import com.fzk.dtu.constant.UpMsgType;
import com.fzk.dtu.utils.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class MessageBuilder {
    private static Charset charset = Charset.forName("gb2312");
    private static AtomicLong serialGenerator = new AtomicLong(0);

    private static int getSerial(){
        Long serialCore = serialGenerator.addAndGet(1);
        Long serialTemp = serialCore%(0xff+1);
        return serialTemp.intValue();
    }

    public static String buildFzkMsg(String sn, String fzkContent, boolean aesEncode){
        byte[] fzkBytes = fzkContent.getBytes(charset);
        ByteBuf buf = Unpooled.buffer(fzkBytes.length + 1);
        buf.writeByte(0x41);
        buf.writeBytes(fzkBytes);
        return buildMsg(sn, UpMsgType.PASSTHROUGH.getMsgId(),buf.array(), aesEncode);
    }

    public static String buildMsg(String sn, int funId, byte[] content, boolean aesEncode){
        ByteBuffer bf = ByteBuffer.allocate(1024);

        /**head部分*/
        ParseUtil.putTwoBytesInt(bf,funId);

        //消息体属性
        //分包
        int packSeparate = 0;
        //加密算法  010 代表aes加密消息
        int encodeAlgo = 0;

        if(aesEncode){
            encodeAlgo = 2;
            try {
                content = AESUtil.encrypt(content);
            } catch (Exception e) {
                log.error("生成回复消息，并进行AES加密时发生异常：{}",e);
            }
        }

        //消息长度 0
        int msgLength = content.length;
        int msgInfo = packSeparate << 13 | ((encodeAlgo & 0x07) << 10) | msgLength & 0x3ff;
        ParseUtil.putTwoBytesInt(bf,msgInfo);

        //手机号->设备序列号
        sn = ZeroFillStrUtil.zeroFillStr(sn,12);
        byte[] snBytes = BCD6Util.ascii2bcd(sn.getBytes(),sn.length());;
        bf.put(snBytes);

        //下行消息流水号
        ParseUtil.putTwoBytesInt(bf,getSerial());

        /**消息内容部分*/
        if(content!=null){
            bf.put(content);
        }

        //需要校验的字节数据
        byte[] validateContent = ParseUtil.getReadableBytes(bf);

        //校验码
        byte validCode = ValidateUtil.getValidCode(validateContent);

        //重新封装，加上起始符，结束符和校验码
        byte[] repackContent =  ParseUtil.repackContent(validateContent,validCode);

        //转义
        byte[] transferContent =  RestoreUtil.transfer(repackContent);


        String finalResp = BytesUtil.bytesToHexShortString(transferContent);

        return finalResp;
    }
}
