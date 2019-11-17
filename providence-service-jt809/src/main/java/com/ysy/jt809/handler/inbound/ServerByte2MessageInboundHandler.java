package com.ysy.jt809.handler.inbound;

import com.ysy.jt809.codec.decoder.Byte2MessageDecoder;
import com.ysy.jt809.config.NettyConfig;
import com.ysy.jt809.manage.TcpChannelMsgManage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author choice
 * 解码处理
 * @date 2019-07-03 09:55
 *
 */
@Slf4j
@Service
@ChannelHandler.Sharable
public class ServerByte2MessageInboundHandler extends ChannelInboundHandlerAdapter {

    @Resource
	private Byte2MessageDecoder decoder;
    @Resource
	private NettyConfig.NettyServerConfig nettyConfig;
    @Resource
	private TcpChannelMsgManage tcpChannelMsgManage;


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if(null != nettyConfig.getMaxConnectNum() && nettyConfig.getMaxConnectNum() > 0){
            //判断当前连接数是否大于 配置的最大连接数 如果大于最大连接数直接关闭连接
            if(tcpChannelMsgManage.getTcpClientConnectNum() > nettyConfig.getMaxConnectNum()){
                log.warn("当前节点连接数已经超过最大连接数 : {}", tcpChannelMsgManage.getTcpClientConnectNum());
                //关闭客户端连接
                ctx.channel().close();
                return;
            }
        }

        //计数器加1
        tcpChannelMsgManage.incrementTcpClientConnectNum();
        log.info("连接到新客户端，当前节点连接数 : {}", tcpChannelMsgManage.getTcpClientConnectNum());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //计数器减1
        tcpChannelMsgManage.decrementTcpClientConnectNum();
        log.info("断开客户端连接,客户端信息 : {}",ctx.channel().remoteAddress().toString());
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        decoder.decode(ctx,byteBuf);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.warn("客户端连接超时,客户端信息 : {}",ctx.channel().remoteAddress().toString());
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("发生错误.......",cause);
        ctx.close();
    }
}
