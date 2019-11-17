package com.ysy.jt809.server;

import com.ysy.jt809.config.NettyConfig;
import com.ysy.jt809.handler.inbound.MessageForwardInboundHandler;
import com.ysy.jt809.handler.inbound.ServerByte2MessageInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author mc
 * Create date 2019/11/10 10:08
 * Version 1.0
 * Description
 */
@Component
public class JT809ServerChannelInit extends ChannelInitializer<SocketChannel> {

	@Resource
	private NettyConfig.NettyServerConfig serverConfig;
	@Resource
	private ServerByte2MessageInboundHandler serverByte2MessageInboundHandler;
	@Resource
	private MessageForwardInboundHandler messageForwardInboundHandler;
	@Override
	protected void initChannel(SocketChannel socketChannel) throws Exception {
		socketChannel.pipeline()
				.addLast(new IdleStateHandler(serverConfig.getReaderIdleTimeSeconds(),serverConfig.getWriterIdleTimeSeconds(),serverConfig.getAllIdleTimeSeconds(), TimeUnit.SECONDS))
				.addLast(serverByte2MessageInboundHandler)
				.addLast(messageForwardInboundHandler);

	}
}
