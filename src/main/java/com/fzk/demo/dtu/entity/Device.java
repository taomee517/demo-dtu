package com.fzk.demo.dtu.entity;

import lombok.Data;

@Data
public class Device {
    public String imei = "623568794561335";
    public String sn = "014533224352";
    public int terminalId = 119210;


//    public String imei = "863744046701693";
//    public String sn = "014533228489";
//    public int terminalId = 45994;



    public int attachId = 6;

    //广东省，深圳市，粤B88888，蓝色， 终端型号：KT-20
    public String regContent = "002C012F37303131314B542D32302020206342440257666501D4C1423838383838";
    public String authKey;

    //升级相关
    //单次请求分片数
    public int signleRequestSize = 8;
    //请求次数
    public int totalRequest;
    //当前分片索引
    public int shardRequestIndex = 1;
    //分片校验索引
    public int shardValidRequestIndex = 1;
    //总分片数
    public int totalShard;

    //version
    public String b101 = "keylss_m.base.105b,2115.debug.1,DY_V2.01.001,vw_2019,40,1,vw,FZK-F8AF82C15D61";

    //ability
    public String b102 = "0111011000000000000,1#";

    //bluetooth clear
    public String b220;

    //bluetooth config
    public String b203;

    //upgrade result
    public String b422 = "1";

    //升级信息 扇区编号-扇区当前软件版本-分片长度-引导固件版本
    public String b803 = "0,2112,100,DY_V2";

    //分片请求
    public String b805;

    //分片校验请求
    public String b807;


}
