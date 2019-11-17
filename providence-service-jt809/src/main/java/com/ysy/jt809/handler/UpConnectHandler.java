package com.ysy.jt809.handler;

import com.alibaba.fastjson.JSONObject;
import com.ysy.jt809.bean.Message;
import com.ysy.jt809.bean.UpConnectReq;
import com.ysy.jt809.codec.encoder.Message2ByteEncoder;
import com.ysy.jt809.config.ChannelManager;
import com.ysy.jt809.config.NettyConfig;
import com.ysy.jt809.constants.JT809DataTypeConstants;
import com.ysy.jt809.constants.JT809ResCodeConstants;
import com.ysy.jt809.manage.TcpChannelMsgManage;
import com.ysy.jt809.util.ByteArrayUtil;
import com.ysy.jt809.util.PacketUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.unix.Buffer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 主链路登陆逻辑处理
 *
 * @author Administrator
 */
@Slf4j
@Component
public class UpConnectHandler implements CommonHandler {

    @Resource
    private NettyConfig.NettyServerConfig serverConfig;
    @Resource
    private Message2ByteEncoder message2ByteEncoder;
    @Resource
    private TcpChannelMsgManage tcpChannelMsgManage;


    @Override
    public void handler(ChannelHandlerContext ctx, Message msg) {
        this.login(ctx, msg);

    }

    /**
     * 登陆逻辑处理
     *
     * @param msg
     */
    private void login(ChannelHandlerContext ctx, Message msg) {
        int index = 0;
        int userId = ByteArrayUtil.bytes2int(ByteArrayUtil.subBytes(msg.getMsgBody(), index, 4));
        index += 4;
        String password = ByteArrayUtil.bytes2gbkString(ByteArrayUtil.subBytes(msg.getMsgBody(), index, 8));
        password = password.replaceAll("\\u0000", "");
        index += 8;
        String downLinkIp = ByteArrayUtil.bytes2gbkString(ByteArrayUtil.subBytes(msg.getMsgBody(), index, 32));
        downLinkIp = downLinkIp.replaceAll("\\u0000", "");
        index += 32;
        int downLinkPort = ByteArrayUtil.bytes2int(ByteArrayUtil.subBytes(msg.getMsgBody(), index, 2));
        index += 2;

        UpConnectReq req = new UpConnectReq();
        req.setUsername(userId);
        req.setPassword(password);
        req.setDownLinkIp(downLinkIp);
        req.setDownLinkPort(downLinkPort);
        log.info("登陆请求信息：" + JSONObject.toJSONString(req));

        byte[] result = new byte[]{JT809ResCodeConstants.UpConnect.SUCCESS};
        byte[] verifyCode = new byte[4];
        msg.getMsgHead().setMsgId((short) JT809DataTypeConstants.UP_CONNECT_RSP);
        if (serverConfig.getUserId() == req.getUsername() && serverConfig.getPassword().equals(password)) {
            log.info("登陆成功");
            byte[] body = ByteArrayUtil.append(result, verifyCode);
            msg.getMsgHead().setMsgLength(msg.getMsgHead().getMsgLength() - msg.getMsgBody().length + body.length);
            msg.setMsgBody(body);
            message2ByteEncoder.encode(ctx,msg);
            tcpChannelMsgManage.addChannel(ctx.channel().id().asLongText(),ctx.channel());
            return;
        }
        if ("".equals(req.getDownLinkIp())) {
            log.info("ip地址不正确");
            result = new byte[]{JT809ResCodeConstants.UpConnect.IP_ERROR};
        } else if (123456 != msg.getMsgHead().getMsgGnssCenterId()) {
            log.info("接入码不正确");
            result = new byte[]{JT809ResCodeConstants.UpConnect.GUSSCENTERID_ERROR};
        } else if (serverConfig.getUserId() != req.getUsername()) {
            log.info("用户没有注册");
            result = new byte[]{JT809ResCodeConstants.UpConnect.USER_NOT_EXIST};
        } else if (!serverConfig.getPassword().equals(password)) {
            log.info("密码错误");
            result = new byte[]{JT809ResCodeConstants.UpConnect.PASSWORD_ERROR};
        } else {
            log.info("其他错误");
            result = new byte[]{JT809ResCodeConstants.UpConnect.OTHER_ERROR};
        }
        byte[] body = ByteArrayUtil.append(result, verifyCode);
        msg.getMsgHead().setMsgLength(msg.getMsgHead().getMsgLength() - msg.getMsgBody().length + body.length);
        msg.setMsgBody(body);
        message2ByteEncoder.encode(ctx,msg);
        ctx.channel().close();
    }
}
