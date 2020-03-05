package com.fzk.demo.dtu.handler;

import com.fzk.demo.dtu.constant.AttachFunType;
import com.fzk.demo.dtu.entity.Device;
import com.fzk.demo.dtu.entity.MessageBasic;
import com.fzk.demo.dtu.util.MessageBuilder;
import com.fzk.demo.dtu.util.ReflectUtil;
import com.fzk.dtu.constant.DownMsgType;
import com.fzk.dtu.constant.UpMsgType;
import com.fzk.dtu.utils.BytesUtil;
import com.fzk.sdk.util.BytesTranUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static com.fzk.demo.dtu.constant.DefaultValue.*;

/**
 * 模拟聊天室客户端消息处理类
 *
 * @Author luotao
 * @E-mail taomee517@qq.com
 * @Date 2019\1\27 0027 16:47
 */
@Slf4j
public class CoreLogicHandler extends ChannelInboundHandlerAdapter {
    private Device device;

    public volatile Set<String> waitShardIndexSet = new HashSet<>();

    private boolean isOnline = false;

    public CoreLogicHandler(Device device) {
        this.device = device;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof MessageBasic){
            MessageBasic in = ((MessageBasic) msg);
            String hexMsg = BytesUtil.bytesToHexShortString(in.raw);
            log.info("RAW-VVV：{}", hexMsg);
            int funId = in.funId;
            if(funId== DownMsgType.TRANSMIT.getMsgId()){
                String fzkContent = new String(in.content);
                log.info("透传↓↓↓↓：{}", fzkContent);
                String[] fzkArray = StringUtils.split(fzkContent,PARA_CONNECTOR);
                String funAndTag = fzkArray[1];
                String[] funTagArray = StringUtils.split(funAndTag,FUN_TAG_CONNECTOR);
                String fun = funTagArray[0];
                String tag = funTagArray[1].toLowerCase();
                String value = StringUtils.substringAfter(fzkContent,funAndTag);
                if (StringUtils.startsWith(value, PARA_CONNECTOR)) {
                    value = StringUtils.substringAfter(value, PARA_CONNECTOR);
                }
                AttachFunType type = AttachFunType.getTypeByFunId(Integer.valueOf(fun));
                switch (type){
                    case SETTING:
                        ReflectUtil.tagSetting(device,tag,value);
                    case QUERY:
                    case PUBLISH:
                        String ack = ReflectUtil.buildAttachMessage(AttachFunType.getTypeByFunId(type.getAckFunId()),device,tag);
                        String packAck = MessageBuilder.buildFzkMsg(device.sn,ack,true);
                        log.info("透传↑↑↑↑：{}", ack);
                        ctx.writeAndFlush(packAck);

                        if (CONTROL_TAG.equalsIgnoreCase(tag)) {
                            //构建控制结果
                            String[] paras = StringUtils.split(value,PARA_CONNECTOR);
                            String bizIndex = paras[0];
                            String serial = paras[1];
                            String result = "1";
                            String resultContent = StringUtils.joinWith(PARA_CONNECTOR,bizIndex,result,serial);
                            String resultTag = RESULT_TAG;
                            ReflectUtil.tagSetting(device,resultTag,resultContent);
                            String resultMsg = ReflectUtil.buildAttachMessage(AttachFunType.PUBLISH,device,resultTag);
                            String packResult = MessageBuilder.buildFzkMsg(device.sn,resultMsg,true);
                            log.info("透传↑↑↑↑：{}", resultMsg);
                            ctx.writeAndFlush(packResult);
                        }
                        break;
                    case WRITE:
                        switch (tag){
                            case "b801":
                                //升级命令
                                String requestTag = "b803";
                                writeMessage(ctx,type,requestTag);
                                break;
                            case "b804":
                                //分片数据
                                String srcTotalShard = StringUtils.split(value,PARA_CONNECTOR)[1];
                                if(StringUtils.isNotEmpty(srcTotalShard)){
                                    device.totalShard = Integer.valueOf(srcTotalShard,16);
                                    if (device.totalShard%device.signleRequestSize==0) {
                                        device.totalRequest = device.totalShard/device.signleRequestSize;
                                    }else {
                                        device.totalRequest = device.totalShard/device.signleRequestSize + 1;
                                    }
                                    String shardRequestParam = getShardRequestParam();
                                    device.b805 = shardRequestParam;
                                    String firstShardTag = "b805";
                                    writeMessage(ctx,type,firstShardTag);
                                    device.shardRequestIndex++;
                                    putWaitShardSet(shardRequestParam);
                                }else {
                                    log.warn("升级文件总分片数为空！imei = {}", device.imei);
                                }

                                break;
                            case "b806":
                                //分片校验
                                String srcShardDataIndex = StringUtils.split(value,PARA_CONNECTOR)[0];
                                if (device.shardRequestIndex <= device.totalRequest) {
                                    CompletableFuture.runAsync(()->{
                                        waitShardIndexSet.remove(srcShardDataIndex);
                                    }).thenAccept(new Consumer<Void>() {
                                        @Override
                                        public void accept(Void aVoid) {
                                            log.info("index = {}, total = {}, set-size = {}", device.shardRequestIndex,device.totalRequest,waitShardIndexSet.size());
                                            if (waitShardIndexSet.size()==0) {
                                                String shardRequestParam = getShardRequestParam();
                                                device.b805 = shardRequestParam;
                                                String shardTag = "b805";
                                                writeMessage(ctx,type,shardTag);
                                                device.shardRequestIndex++;
                                                putWaitShardSet(shardRequestParam);
                                            }
                                        }
                                    });
                                }else {
                                    CompletableFuture.runAsync(()->{
                                        waitShardIndexSet.remove(srcShardDataIndex);
                                    }).thenAccept(new Consumer<Void>() {
                                        @Override
                                        public void accept(Void aVoid) {
                                            if (waitShardIndexSet.size()==0) {
                                                String startHexIndex = Integer.toHexString(0);
                                                String endHexIndex = Integer.toHexString(7);
                                                String shardValidRequestParam = StringUtils.join(startHexIndex, PARA_CONNECTOR, endHexIndex);
                                                device.b807 =shardValidRequestParam;
                                                String firstValidTag = "b807";
                                                writeMessage(ctx, type, firstValidTag);
                                                device.shardValidRequestIndex++;
                                            }
                                        }
                                    });
                                }
                                break;
                            case "b808":
                                if(device.shardValidRequestIndex <= device.totalRequest){
                                    String startHexIndex = Integer.toHexString((device.shardValidRequestIndex-1)*8);
                                    String endHexIndex = Integer.toHexString(device.shardValidRequestIndex*8-1);
                                    if (device.shardValidRequestIndex==device.totalRequest) {
                                        endHexIndex = Integer.toHexString(device.totalShard-1);
                                    }
                                    String shardValidRequestParam = StringUtils.join(startHexIndex,PARA_CONNECTOR,endHexIndex);
                                    device.b807 = shardValidRequestParam;
                                    String validTag = "b807";
                                    writeMessage(ctx,type,validTag);
                                    device.shardValidRequestIndex++;
                                }else {
                                    String upgradeResult = ReflectUtil.buildAttachMessage(AttachFunType.PUBLISH,device,"b422");
                                    String packResult = MessageBuilder.buildFzkMsg(device.sn,upgradeResult,true);
                                    log.info("透传↑↑↑↑：{}", upgradeResult);
                                    ctx.writeAndFlush(packResult);
                                    return;
                                }
                                break;
                            default:
                                log.info("不需要处理的消息类型，fun = {} tag = {}", fun, tag);
                                return;
                        }
                        break;
                    default:
                        return;
                }
            }else {
                ByteBuffer buffer = ByteBuffer.wrap(in.content);
                DownMsgType type = DownMsgType.getProtocolByMsgId(funId);
                switch (type){
                    case RESPONSE:
                        byte srcResult = buffer.get(4);
                        if(!isOnline && srcResult==0){
                            isOnline = true;
                            log.info("登录成功！");
                            String versionContent = ReflectUtil.buildFzkContent(device.attachId,AttachFunType.PUBLISH.getFunId(),VERSION_TAG,device.b101);
                            String versionMsg = MessageBuilder.buildFzkMsg(device.sn,versionContent,true);
                            log.info("透传↑↑↑↑：{}", versionContent);
                            ctx.writeAndFlush(versionMsg);

                            String abilityContent = ReflectUtil.buildFzkContent(device.attachId,AttachFunType.PUBLISH.getFunId(),ABILITY_TAG,device.b102);
                            String abilityMsg = MessageBuilder.buildFzkMsg(device.sn,abilityContent,true);
                            log.info("透传↑↑↑↑：{}", abilityContent);
                            ctx.writeAndFlush(abilityMsg);
                        }
                        break;
                    case REGISTER_ACK:
                        byte[] srcAuth = new byte[in.content.length-3];
                        buffer.position(3);
                        buffer.get(srcAuth);
                        String authKey = new String(srcAuth,CHARSET);
                        device.authKey = authKey;
                        if (!isOnline) {
                            String loginMsg = buildLoginMessage();
                            log.info("模拟登录");
                            ctx.writeAndFlush(loginMsg);
                        }
                        break;
                    case SETTING:
                        byte[] content = in.content;
                        byte[] srcParaId = new byte[2];
                        System.arraycopy(content,0,srcParaId,0,srcParaId.length);
                        byte srcLength = content[2];
                        byte[] srcConfig = new byte[srcLength];
                        System.arraycopy(content,3,srcConfig,0,srcLength);
                        String config = new String(srcConfig,CHARSET);
                        log.info("下发配置 paraId = {}, config = {}", BytesUtil.twoBytesToInt(srcParaId), config);
                        String ack = buildCommonAck(in.serial,in.funId);
                        ctx.writeAndFlush(ack);
                        break;
                    case QUERY:
                        byte[] querySerial = BytesUtil.intToTwoBytes(in.serial);
                        ByteBuffer queryBuffer = ByteBuffer.allocate(1024);
                        queryBuffer.put(querySerial);
                        //两个参数，host-0013  port-0018
                        queryBuffer.put(((byte) 2));

                        String addr = StringUtils.substringAfter(ctx.channel().remoteAddress().toString(),"/");
                        String host = StringUtils.substringBefore(addr, ":");
                        String strPort = StringUtils.substringAfter(addr, ":");
                        byte[] hostBytes = host.getBytes(CHARSET);
                        byte[] portBytes = BytesUtil.intToTwoBytes(Integer.parseInt(strPort));
                        //host
                        byte[] hostParaId = {0,0,0,19};
                        queryBuffer.put(hostParaId);
                        queryBuffer.put(((byte) hostBytes.length));
                        queryBuffer.put(hostBytes);
                        //port
                        byte[] portParaId = {0,0,0,24};
                        queryBuffer.put(portParaId);
                        queryBuffer.put(((byte) portBytes.length));
                        queryBuffer.put(portBytes);
                        byte[] queryAckContent = new byte[queryBuffer.position()];
                        queryBuffer.flip();
                        queryBuffer.get(queryAckContent);
                        queryBuffer.clear();
                        String queryAck = MessageBuilder.buildMsg(device.sn,UpMsgType.QUERY_RESULT.getMsgId(), queryAckContent,true);
                        ctx.writeAndFlush(queryAck);
                        break;
                    default:
                        log.info("暂时不支持的消息类型， funId = {}", funId);
                        break;
                }
            }
        }
        ctx.fireChannelRead(msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent event = (IdleStateEvent)evt;
        if(event.equals(IdleStateEvent.WRITER_IDLE_STATE_EVENT)){
            String heatbeat = MessageBuilder.buildMsg(device.sn,UpMsgType.HEART_BEAT.getMsgId(),null,true);
            log.info("C ——> S");
            ctx.writeAndFlush(heatbeat);
        }
    }



    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        boolean randomFlag = new Random().nextBoolean();
        String first = null;
        if (randomFlag) {
            byte[] registerContent = BytesTranUtil.hexStringToBytes(device.regContent);
            String registerMsg = MessageBuilder.buildMsg(device.sn, UpMsgType.REGISTER.getMsgId(),registerContent,true);
            first = registerMsg;
            log.info("模拟注册");
        }else {
            first = buildLoginMessage();
            log.info("模拟登录");
        }

        //线上设备测试
//        String first = "7E 01 02 08 20 01 45 33 22 84 89 00 27 46 09 7D 01 F6 86 5E 66 E4 11 B6 AD 8A EE 92 D3 11 BD F8 99 CE B9 A2 9D 13 D8 87 B1 16 B0 AC C1 E2 B4 7E";
//        first = StringUtils.replace(first, " ", "");

        ctx.writeAndFlush(first);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("CoreLogicHandler发生异常：", cause);
        ctx.close();
    }



    private String buildLoginMessage(){
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeBytes(device.authKey.getBytes(CHARSET));
        buffer.writeBytes(BytesTranUtil.str2Bcd(device.imei));
        byte[] authContent = new byte[buffer.readableBytes()];
        buffer.readBytes(authContent);
        String authMsg = MessageBuilder.buildMsg(device.sn,UpMsgType.AUTH.getMsgId(),authContent,true);
        buffer.release();
        return authMsg;
    }


    private String buildCommonAck(int serverSerial, int funId){
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeBytes(BytesUtil.intToTwoBytes(serverSerial));
        buffer.writeBytes(BytesUtil.intToTwoBytes(funId));
        //默认回复0，成功
        buffer.writeByte(0);
        byte[] ackContent = new byte[buffer.readableBytes()];
        buffer.readBytes(ackContent);
        String authMsg = MessageBuilder.buildMsg(device.sn,UpMsgType.ACK.getMsgId(),ackContent,true);
        buffer.release();
        return authMsg;
    }


    private void putWaitShardSet(String shardRequestParam){
        String[] shardRequestIndexs = StringUtils.split(shardRequestParam,PARA_CONNECTOR);
        for(String index: shardRequestIndexs){
            waitShardIndexSet.add(index);
        }
    }


    private String getShardRequestParam(){
        if (device.shardRequestIndex < (device.totalRequest-1)) {
            String shardRequestParam = IntStream.range(8*device.shardRequestIndex,8*(device.shardRequestIndex+1))
                    .mapToObj(i->Integer.toHexString(i))
                    .reduce((a,b)->StringUtils.joinWith(PARA_CONNECTOR,a,b))
                    .get();
            return shardRequestParam;
        }if(device.shardRequestIndex == (device.totalRequest-1)) {
            return IntStream.range(8*device.shardRequestIndex,device.totalShard+1)
                    .mapToObj(i->Integer.toHexString(i))
                    .reduce((a,b)->StringUtils.joinWith(PARA_CONNECTOR,a,b))
                    .get();
        }else {
            return IntStream.range(0,8)
                    .mapToObj(i->Integer.toHexString(i))
                    .reduce((a,b)->StringUtils.joinWith(PARA_CONNECTOR,a,b))
                    .get();
        }
    }


    private void writeMessage(ChannelHandlerContext ctx, AttachFunType type, String requestTag){
        try {
            String upgradeRequest = ReflectUtil.buildAttachMessage(AttachFunType.getTypeByFunId(type.getAckFunId()),device,requestTag);
            String packRequest = MessageBuilder.buildFzkMsg(device.sn,upgradeRequest,true);
            log.info("透传↑↑↑↑：{}", upgradeRequest);
            ctx.writeAndFlush(packRequest);
        } catch (Exception e) {
            log.error("发送消息时发生异常：e ={}", e);
        }
    }


}
