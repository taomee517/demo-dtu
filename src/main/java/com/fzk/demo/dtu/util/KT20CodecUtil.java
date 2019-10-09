/**
 *
 */
package com.fzk.demo.dtu.util;

import com.fzk.dtu.utils.BytesUtil;
import com.fzk.dtu.utils.RestoreUtil;
import com.fzk.sdk.util.BytesTranUtil;
import io.netty.buffer.ByteBuf;
import io.netty.util.ByteProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Luo Tao
 */
@Slf4j
public class KT20CodecUtil {
    /**开始、结束符*/
    private static final byte SIGN_CODE = 0x7e;

    /**数据包最小长度:起始位(1) + 消息id(2) + 消息体属性(2) + 终端手机号(6) + 消息流水号（2） + 校验码(1) + 停止位(1)*/
    private static final int MIN_LENGTH = 1 + 2 + 2 + 6 + 2 + 1 + 1;

    /**
     * bytebuf 装维ascii码  字符串
     *
     * @param in
     * @return
     * @throws Exception
     */
    public static String Byte2StringSerialize(ByteBuf in) throws Exception {
        int readableLen = in.readableBytes();
        if (readableLen < MIN_LENGTH) {
            return null;
        }
        int startSignIndex = in.forEachByte(new ByteProcessor.IndexOfProcessor(SIGN_CODE));
        if(startSignIndex==-1){
            return null;
        }
        //将readerIndex置为起始符下标+1
        //因为起始符结束符是一样的，如果不往后移一位，下次到的还是起始下标
        in.readerIndex(startSignIndex + 1);

        //找到第一个报文结束符的下标
        int endSignIndex = in.forEachByte(new ByteProcessor.IndexOfProcessor(SIGN_CODE));
        if(endSignIndex == -1 || endSignIndex < startSignIndex){
            in.readerIndex(startSignIndex);
            return null;
        }


        //计算报文的总长度
        //此处不能去操作writerIndex,否则只能截取到第一条完整报文
        int length = endSignIndex + 1 - startSignIndex;

        //如果长度还小于最小长度，就丢掉这条消息
        if(length < MIN_LENGTH){
            byte[] errMsg = new byte[length];
            for(int i= startSignIndex; i< (endSignIndex + 1); i++){
                int errIndex = i-startSignIndex;
                errMsg[errIndex] = in.getByte(i);
            }
            log.error("异常消息，有分隔符但长度太短：{}", BytesUtil.bytesToHexShortString(errMsg));
            in.readerIndex(endSignIndex);
            return null;
        }

        //将报文内容写入符串，并返回
        byte[] data = new byte[length];
        in.readerIndex(startSignIndex);
        in.readBytes(data);
        //转义还原
        data = RestoreUtil.restore(data);
        return BytesUtil.bytesToHexString4BSJ(data).toUpperCase();
    }


}
