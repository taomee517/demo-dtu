//package com.fzk.demo.dtu.handler.discard;
//
//import com.blackTea.common.constants.ClientTypeEnum;
//import com.fzk.demo.dtu.constant.DefaultValue;
//import com.fzk.demo.dtu.util.MessageBuilder;
//import com.fzk.dtu.constant.DtuConstants;
//import com.fzk.dtu.utils.ProtocolSwitchUtil;
//import com.fzk.otu.utils.ConvertUtil;
//import com.fzk.redis.model.CacheNameEnum;
//import com.fzk.redis.model.M_Terminal;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.ChannelInboundHandlerAdapter;
//import io.netty.handler.timeout.IdleStateEvent;
//import kv.c.KV_Int;
//import kv.m.KV_Status;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//
///**
// * 模拟聊天室客户端消息处理类
// *
// * @Author luotao
// * @E-mail taomee517@qq.com
// * @Date 2019\1\27 0027 16:47
// */
//@Slf4j
//public class DeviceHandler extends ChannelInboundHandlerAdapter {
//    private int index;
//    private int shardIndex;
//    private int shardValidIndex;
//
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        String serverMsg = msg.toString();
//
//        if (index==0) {
//            String imei = "623568794561335";
////            String imei = "863744046701461";
//            M_Terminal mTerminal = M_Terminal.dao.find(CacheNameEnum.TERMINAL_IMEI, imei);
//            mTerminal.setIsOnline(true);
//            mTerminal.saveOrUpdate(CacheNameEnum.TERMINAL_IMEI, imei);
//
//            //本地数据库测试 teminalId = 119210
//            int terminalId = 119210;
//            //6.160数据库 terminalId = 156816;
////            int terminalId = 156816;
//            KV_Status status = KV_Status.dao.find(ClientTypeEnum.DTU.getClientType(),terminalId);
//            status.setAttachOnline(new KV_Int(1));
//            status.saveOrUpdate(ClientTypeEnum.DTU.getClientType(),terminalId);
////            byte[] contetnt = (DtuConstants.DEFAULT_AUTH_CODE +  "0863744046701461").getBytes(DtuConstants.CHARSET);
////            String up = MessageBuilder.buildMsg("14533224352","0102",contetnt,89,true);
//
//            //登录消息 - 623568794561335
//            String up = "7E 01 02 08 20 01 45 33 22 43 52 00 21 46 09 7D F6 86 5E 66 E4 11 B6 AD 8A EE 92 D3 11 42 EC 8B 51 D6 31 9A E7 2A 6F C8 56 C4 3F C1 6F 8E 7E";
//
//            //登录消息 - 868288049818489
////        String up = "7E01020820015018513878000146097DF6865E66E411B6AD8AEE92D311C96ADB7EC9EE218D3059A698D1720193397E";
//
//            //3,7#b401,1,2
////            String up = "7E0900000D014533224352000441332C3723623430312C312C325A7E";
//            //3,1#b709,
////            String up = "7E0900000A014533224352000441332C3123623730392C7F7E";
//            //鉴权消息
////            String up = "7E010200140145332243520002465A4B2D42534A2D4B5432302D434F4E4649524D3F7E";
//            //定位消息
////            String up = "7E0200002201453322435200030000000000000001015F1FDA06D0AA40000000000000190812195834010400000000B27E";
////            String temp = "7E 02 00 00 3C 01 45 33 22 43 52 0B 1B 00 00 00 00 80 12 40 02 01 C4 11 D0 06 58 D8 90 02 32 00 00 00 C8 19 09 10 12 58 38 01 04 00 00 00 07 02 08 00 00 00 00 00 00 00 00 BC 0E 00 0C 00 B2 89 86 04 41 19 18 C3 61 50 10 8F 7E";
////            String up = temp.replaceAll(" ", "");
//            //告警消息
////            String up = "7E0900000E014533224352000441362C3723623431332C332C31237D7E";
////            String temp = "7E 09 00 00 0E 01 45 33 22 43 52 00 76 41 36 2C 37 23 62 34 31 33 2C 33 2C 31 23 0F 7E*7E0900000E014533224352007641(6,7#b413,3,1#)0F7E";
//            //心跳消息
////            String up = "7E000200000145332243520004427E";
//            //登出消息
////            String up = "7E000300000145332243520004437E";
//            //查询结果
////            String up = "7E01040188013620125135032300002B0000000104000000B40000000204000000000000000304000000000000000404000000000000000504000000000000000604000000000000000704000000000000001005434D4E455400000011000000001200000000130E3132332E36352E3231362E323436000000140000000015000000001600000000170E3132332E36352E3231362E32343600000018040000226600000019040000000000000020040000000000000021040000000000000022040000000000000027040000007800000028040000000000000029040000001E0000002C040000000000000050040000000000000052040000000000000053040000000000000055040000007800000056040000000A00000057040000000000000058040000000000000059040000003C0000005A04000000000000007004000000000000007104000000000000007204000000000000007304000000000000007404000000000000008004000000000000008102002C0000008202012F000000830CD4C142383838383820202020000000840101897E";
//            //设备查询 b733
////            String up = "7E09000048014533224352000441362C3323623733332C4C53565547363054364B323039333435382C322C302C342C302C34623139656461633530396430643432633866343532393363663563373837622C6661230E7E";
//           //设备查询 b731
////            String up = "7E0900001F014533224352000441362C3723623733312C4C5356555A363054394A323230393736322C313223337E";
//            //设备查询b734
////            String up = "7E0900000B014533224352000441362C3123623733342C23567E";
//
//            log.info("模拟设备消息: {}", up);
//            ctx.channel().writeAndFlush(up);
//            index++;
//        }else if(index == 1){
//            String up = "7E0900000E014533224352000441362C3723623431332C332C31237D7E";
//
//            //设备查询 b733
////            String up = "7E09000048014533224352000441362C3323623733332C4C53565544363054354A323136303236362C322C302C342C302C34623139656461633530396430643432633866343532393363663563373837622C666123097E";
//            //设备查询 b731
////            String up = "7E0900001E014533224352000441362C3723623733312C4C53565544363054354A323136303236362C63234D7E";
//            //设备查询b734
////            String up = "7E0900000B014533224352000441362C3123623733342C23567E";
//
//            log.info("模拟设备透传消息: {}", up);
//            ctx.channel().writeAndFlush(up);
//            index++;
//        }else if(index == 3){
//            //b413告警消息
//            String up = "7E0900000E014533224352000441362C3723623431332C332C31237D7E";
//            ctx.channel().writeAndFlush(up);
//        }
//
//
//
//        if(serverMsg.contains("7E80010005")){
//            log.info("C <—— S");
//            serverMsg = null;
//        }else if(serverMsg.contains("7E8201")){
//            //查询位置
//            log.info("查询主机定位指令: {}", serverMsg );
//            serverMsg = null;
//            String location = "7E0201003E01453322435200487256000000008012400201C411D00658D8900232000000C819091012583801040000000702080000000000000000BC0E000C00B2898604411918C3615010F07E";
////            String location = "7E0201002401453322435200487256000000000000000101C4D08C06583900000000000000190826182715010400000000E87E";
//            log.info("回复位置：{}", location);
//            ctx.channel().writeAndFlush(location);
//            //查询能力
//        }else if(serverMsg.contains("312362313032")){
//            log.info("查询能力指令: {}", serverMsg );
//            serverMsg = null;
//            String ability = "7E0900001E014533224352000141362C3223623130322C3031313130313131313030303030303030303023777E";
//            log.info("回复能力：{}", ability);
//            ctx.channel().writeAndFlush(ability);
//            //控制
//        }else if(serverMsg.contains("372362353031")){
//            log.info("控车指令: {}", serverMsg );
//            serverMsg = null;
//            //目前写死，只回复解锁结果
////            String result = "7E0900000E014533224352037B41362C3723623430312C322C3223007E";
//            //无能力时的回复
////            String result = "7E0900000E014533224352037B41362C3723623430312C322C3623047E";
//            //错误码为b,测试
//            String result = "7E0900000D014533224352000441362C3723623430312C312C620F7E";
//            log.info("回复控制结果：{}", result);
//            ctx.channel().writeAndFlush(result);
//        }else if(serverMsg.contains("332362323230")){
//            log.info("蓝牙清除指令: {}", serverMsg );
//            serverMsg = null;
//            String clearBtCfgResp = "7E09000009014533224352698F41362C342362323230BC7E";
//            log.info("回复清除蓝牙配置：{}", clearBtCfgResp);
//            ctx.channel().writeAndFlush(clearBtCfgResp);
//        }else if(serverMsg.contains("332362323033")){
//            log.info("蓝牙设置指令: {}", serverMsg );
//            serverMsg = null;
//            String setBtCfgResp = "7E09000014014533224352C2EC41362C3423623230332C324E393253322C316364487E";
//            log.info("回复蓝牙配置结果：{}", setBtCfgResp);
//            ctx.channel().writeAndFlush(setBtCfgResp);
//            log.info("蓝牙设置成功! ");
//        }else if(serverMsg.contains("312362313031")){
//            log.info("查询版本指令: {}", serverMsg );
//            serverMsg = null;
//            String versionResp = "7E0900005A014533224352000841362C3223623130312C6B65796C73735F6D2E626173652E313035622C323131332E64656275672E312C44595F56322E30302E3030312C76775F71335F323031372C34302C312C312C465A4B2D45374245414132363833413323297E";
//            versionResp = versionResp.replaceAll(" ", "");
//            log.info("回复版本信息：{}", versionResp);
//            ctx.channel().writeAndFlush(versionResp);
//        }else if(serverMsg.contains("312362343133")){
//            String alarmResp = "7E 09 00 00 0E 01 45 33 22 43 52 00 76 41 36 2C 37 23 62 34 31 33 2C 33 2C 31 23 0F 7E";
//            ctx.channel().writeAndFlush(alarmResp.replaceAll(" ", ""));
//            log.info("回复告警消息：{}", alarmResp);
//        }else if(serverMsg.contains("352362383031")){
//            ProtocolSwitchUtil.bsj2Fzk(serverMsg);
//            log.info("服务器升级指令: fzk = {},  bsj = {}", serverMsg );
//            serverMsg = null;
//            //分片大小256
//            String upgradeReq = "7E0900001A014533224352000441362C3523623830332C312C323131322C3130302C44595F5632617E";
//            //分片大小512
////            String upgradeReq = "7E0900001A014533224352000441362C3523623830332C312C323131322C3230302C44595F5632627E";
//            upgradeReq = upgradeReq.replaceAll(" ", "");
//            ctx.channel().writeAndFlush(upgradeReq);
//            log.info("设备发送升级请求：{}", upgradeReq);
//        }else if(serverMsg.contains("352362383032")){
//            log.info("收到中止升级指令: {}", serverMsg );
//            serverMsg = null;
//        }else if(serverMsg.contains("352362383034")){
//            log.info("收到服务器升级信息反馈：{}", serverMsg);
//            serverMsg = null;
//            String shardReq = "7E0900000D014533224352000441362C3523623830352C312C32557E";
//            log.info("设备发送分片请求：{}", shardReq);
//            ctx.channel().writeAndFlush(shardReq);
//        }else if(serverMsg.contains("352362383036")){
//            shardIndex ++;
//            log.info("收到分片内容{}：{}",shardIndex, serverMsg);
//            serverMsg = null;
//            if (shardIndex==2) {
//                //分片大小-256
//                String shardReq = "7E09000011014533224352000441362C3523623830352C3165662C3166301F7E";
//                //分片大小-512 最大分片数-1
////                String shardReq = "7E0900000F014533224352000441362C3523623830352C66362C6637557E";
//                //分片大小-512 最大分片数
////                String shardReq = "7E0900000F014533224352000441362C3523623830352C66372C66385B7E";
//                log.info("设备发送最后分片请求：{}", shardReq);
//                ctx.channel().writeAndFlush(shardReq);
//            }
////            if(shardIndex == 2){
////                //升级错误
////                String updError = "7E09000014014533224352000441362C3723623432322C32612C65642C663332762B7E";
////                log.info("设备升级时发生错误，车辆状态变成ON：{}", updError);
////                ctx.channel().writeAndFlush(updError);
////            }
//            else if(shardIndex==4){
//                String shardValidReq = "7E0900000D014533224352000441362C3523623830372C312C32577E";
//                log.info("设备发送分片校验请求：{}", shardValidReq);
//                ctx.channel().writeAndFlush(shardValidReq);
//            }
//        }else if(serverMsg.contains("352362383038")){
//            shardValidIndex ++;
//            log.info("收到分片校验信息：{}", serverMsg);
//            serverMsg = null;
//            if (shardValidIndex == 1) {
//                //分片大小-256
//                String shardReq = "7E09000011014533224352000441362C3523623830372C3165662C3166301D7E";
//                //分片大小-512
////                String shardReq = "7E0900000F014533224352000441362C3523623830372C66362C6637577E";
//                //分片大小-512  最大分片数
////                String shardReq = "7E0900000F014533224352000441362C3523623830372C66372C6638597E";
//                log.info("设备发送最后分片校验请求：{}", shardReq);
//                ctx.channel().writeAndFlush(shardReq);
//            }else if(shardValidIndex > 1){
//                String updResult = "7E09000013014533224352000441362C3723623432322C312C65642C663332764E7E";
//                log.info("设备上报升级结果：{}", updResult);
//                ctx.channel().writeAndFlush(updResult);
//            }
//        }
//        if (StringUtils.isNotEmpty(serverMsg)) {
//            log.info("服务器消息: {}", serverMsg);
//        }
//    }
//
//    @Override
//    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//        IdleStateEvent event = (IdleStateEvent)evt;
//        if(event.equals(IdleStateEvent.WRITER_IDLE_STATE_EVENT)){
//            String heatbeat = "7E000200000145332243520004427E";
//            log.info("C ——> S");
//            ctx.channel().writeAndFlush(heatbeat);
//        }
//    }
//
//
//
//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        //注册消息 - 623568794561335
//        String in = "7E010000210145332243520001002C012F37303131314B542D32302020206342440257666501D4C14238383838381C7E";
//        //注册消息 - 868288049818489
////        String in = "7E01020820015018513878000146097DF6865E66E411B6AD8AEE92D311C96ADB7EC9EE218D3059A698D1720193397E";
//
//        //注册消息 - 868288049818711
//
//        log.info("模拟设备注册消息: {}", in);
//        ctx.channel().writeAndFlush(in);
//    }
//
//
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        log.info("DeviceHandler发生异常：", cause);
//        ctx.close();
//    }
//}
