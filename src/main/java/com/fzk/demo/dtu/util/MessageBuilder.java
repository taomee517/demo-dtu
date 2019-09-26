package com.fzk.demo.dtu.util;

import com.fzk.dtu.utils.*;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

@Slf4j
public class MessageBuilder {

    public static String buildMsg(String snNo, String srcMsgId, byte[] content, int serial, boolean aesEncode){
        ByteBuffer bf = ByteBuffer.allocate(1024);

        /**head部分*/
        //msgid
        int downMsgId = Integer.valueOf(srcMsgId,16);
        ParseUtil.putTwoBytesInt(bf,downMsgId);

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
        snNo = ZeroFillStrUtil.zeroFillStr(snNo,12);
        byte[] sn = BCD6Util.ascii2bcd(snNo.getBytes(),snNo.length());;
        bf.put(sn);

        //下行消息流水号
        ParseUtil.putTwoBytesInt(bf,serial);

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
