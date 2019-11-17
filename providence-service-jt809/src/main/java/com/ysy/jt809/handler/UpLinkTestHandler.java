package com.ysy.jt809.handler;

import com.ysy.jt809.bean.Message;
import com.ysy.jt809.codec.encoder.Message2ByteEncoder;
import com.ysy.jt809.constants.JT809DataTypeConstants;
import com.ysy.jt809.manage.TcpChannelMsgManage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 主链路链接保持处理
 *
 * @author Administrator
 */
@Slf4j
@Component
public class UpLinkTestHandler implements CommonHandler{

    @Resource
    private Message2ByteEncoder message2ByteEncoder;
    @Resource
    private TcpChannelMsgManage tcpChannelMsgManage;

    @Override
    public void handler(ChannelHandlerContext ctx, Message msg) {
        log.info("接收到心跳包");
        Channel channel = tcpChannelMsgManage.getChannel(ctx.channel().id().asLongText());
        if (channel == null){
            log.info("非法心跳包");
            ctx.channel().close();
            return;
        }
        msg.getMsgHead().setMsgId((short) JT809DataTypeConstants.UP_LINKTEST_RSP);
        message2ByteEncoder.encode(ctx,msg);
    }
}