package com.ysy.jt809.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.apache.commons.codec.binary.Base64;
import java.io.UnsupportedEncodingException;

/**
 * 主链路登陆请求
 * @author Administrator
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpConnectReq {
    private int username;
    private String password;
    private String downLinkIp;
    private int downLinkPort;

}

