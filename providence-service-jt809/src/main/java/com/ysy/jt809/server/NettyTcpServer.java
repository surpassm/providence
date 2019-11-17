package com.ysy.jt809.server;

import com.ysy.jt809.config.BusinessConfig;
import com.ysy.jt809.config.NettyConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

/**
 * @author mc
 * Create date 2019/11/10 10:03
 * Version 1.0
 * Description
 */
@Slf4j
@Component
public class NettyTcpServer {

	@Resource
	private NettyConfig.NettyServerConfig serverConfig;
	@Resource
	private NettyConfig.NettyClientConfig clientConfig;

	private final NioEventLoopGroup bossGroup;

	private final NioEventLoopGroup workerGroup;

	private final EventExecutorGroup businessGroup;

	@Resource
	private JT809ServerChannelInit jt809ServerChannelInit;

	@Resource
	private BusinessConfig businessConfig;

	@Resource
	private JT809ClientChannelInit jt809ClientChannelInit;

	public static Channel clientChannel;

	public NettyTcpServer(@Qualifier("businessGroup") EventExecutorGroup businessGroup,
						  @Qualifier("workerGroup") NioEventLoopGroup workerGroup,
						  @Qualifier("bossGroup") NioEventLoopGroup bossGroup) {
		this.businessGroup = businessGroup;
		this.workerGroup = workerGroup;
		this.bossGroup = bossGroup;
	}

	@PostConstruct
	public void start() throws InterruptedException {
		ServerBootstrap serverBootstrap = new ServerBootstrap();
		serverBootstrap.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(jt809ServerChannelInit)
				//服务端可连接队列数,对应TCP/IP协议listen函数中backlog参数
				.option(ChannelOption.SO_BACKLOG, 1024)
				//设置tcp缓冲区
				.option(ChannelOption.SO_BACKLOG, 1024)
				//设置发送缓冲大小
				.option(ChannelOption.SO_SNDBUF, 32*1024)
				//这是接收缓冲大小
				.option(ChannelOption.SO_RCVBUF, 32*1024)
				//保持连接
				.option(ChannelOption.SO_KEEPALIVE, true)
				.handler(new LoggingHandler(LogLevel.INFO))	;
		//内存泄漏检测 开发推荐PARANOID 线上SIMPLE
		ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.SIMPLE);
		ChannelFuture future = serverBootstrap.bind(serverConfig.getTcpPort()).sync();
		if (future.isSuccess()) {
			log.info("TCP服务启动完毕,port={}", serverConfig.getTcpPort());
		}
		if(businessConfig.getIsOpenClient()){
			//启动从链路
			this.runClient(future.channel().eventLoop());
		}
	}

	/**
	 * 销毁资源
	 */
	@PreDestroy
	public void destroy() {
		bossGroup.shutdownGracefully().syncUninterruptibly();
		workerGroup.shutdownGracefully().syncUninterruptibly();
		businessGroup.shutdownGracefully().syncUninterruptibly();
		log.info("TCP服务关闭成功");
	}


	/**
	 * 从链路（客户端）引导入口
	 */
	private void runClient(EventLoopGroup group){
		String ip = clientConfig.getTcpIp();
		Integer port = clientConfig.getTcpPort();
		try {
			Bootstrap client = new Bootstrap();
			client.group(group);
			client.channel(NioSocketChannel.class);
			client.option(ChannelOption.TCP_NODELAY, true);
			client.handler(jt809ClientChannelInit);
			ChannelFuture channelFuture = client.connect(ip, port).sync();
			channelFuture.addListener((GenericFutureListener) future -> {
				if (future.isSuccess()) {
					log.info("从链路服务启动成功,TCP-IP:{},TCP-PORT:{}", ip, port);
					clientChannel = channelFuture.channel();
				}
			});
		}catch (Exception e){
			log.error("从链路服务启动失败");
			e.printStackTrace();
		}
	}
}
