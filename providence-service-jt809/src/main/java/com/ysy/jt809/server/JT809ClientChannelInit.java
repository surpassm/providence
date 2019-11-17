package com.ysy.jt809.server;

import com.ysy.jt809.config.NettyConfig;
import com.ysy.jt809.handler.inbound.ClientByte2MessageInboundHandler;
import com.ysy.jt809.handler.inbound.MessageForwardInboundHandler;
import com.ysy.jt809.handler.outbound.Message2ByteOutboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
public class JT809ClientChannelInit extends ChannelInitializer<SocketChannel> {
    @Resource
    private NettyConfig.NettyClientConfig clientConfig;
    @Resource
	private Message2ByteOutboundHandler message2ByteOutboundHandler;
    @Resource
	private ClientByte2MessageInboundHandler clientByte2MessageInboundHandler;
    @Resource
	private MessageForwardInboundHandler messageForwardInboundHandler;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(new IdleStateHandler(clientConfig.getReaderIdleTimeSeconds(),clientConfig.getWriterIdleTimeSeconds(),clientConfig.getAllIdleTimeSeconds(), TimeUnit.SECONDS));
        socketChannel.pipeline().addLast(message2ByteOutboundHandler);
        socketChannel.pipeline().addLast(clientByte2MessageInboundHandler);
        socketChannel.pipeline().addLast(messageForwardInboundHandler);
    }
}
