package com.fzk.demo.dtu.constant;

import java.nio.charset.Charset;

public class DefaultValue {
    public static final String DEFAULT_FIRST_MSG = "Hello,World!";
    public static final String NOT_FOUND = "404";
    public static final String DEFAULT_TEST_IP = "127.0.0.1";
    public static final int DEFAULT_TEST_PORT = 8001;
    /**默认字符集*/
    public static final Charset CHARSET = Charset.forName("GB2312");
    /**默认Netty Boss/Worker线程数*/
    public static final int DEFAULT_BOSS_THREAD = 1;
    public static final int DEFAULT_WORKER_THREAD = 16;
    /**单条消息最大长度限制*/
    public static final int MSG_MAX_SIZE = 512;

    public static final String PARA_CONNECTOR = ",";
    public static final String FUN_TAG_CONNECTOR = "#";


    public static final String VERSION_TAG = "b101";
    public static final String ABILITY_TAG = "b102";

    public static final int PUBLISH_FUN = 7;

}
