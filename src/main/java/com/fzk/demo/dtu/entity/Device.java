package com.fzk.demo.dtu.entity;

import lombok.Data;

@Data
public class Device {
    public String imei = "623568794561335";
    public String sn = "014533224352";

    public int terminalId = 119210;
    public int attachId = 6;

    //广东省，深圳市，粤B88888，蓝色， 终端型号：KT-20
    public String regContent = "002C012F37303131314B542D32302020206342440257666501D4C1423838383838";
    public String authKey;

    //b101
    public String attachVersion = "keylss_m.base.105b,2115.debug.1,DY_V2.01.001,vw_2019,40,1,vw,FZK-F8AF82C15D61";

    //b102
    public String ability = "0111011000000000000,1#";
}
