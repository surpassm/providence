package com.ysy.jt809.constants;

/**
 * 消息应答代码
 */
public class JT809ResCodeConstants {

    /**
     * 主链路登陆应答代码
     */
    public static class UpConnect{
        public static final int SUCCESS = 0x00;
        public static final int IP_ERROR = 0x01;
        public static final int GUSSCENTERID_ERROR = 0x02;
        public static final int USER_NOT_EXIST = 0x03;
        public static final int PASSWORD_ERROR = 0x04;
        public static final int CONNECT_ERROR = 0x05;
        public static final int OTHER_ERROR = 0x06;
    }
}
