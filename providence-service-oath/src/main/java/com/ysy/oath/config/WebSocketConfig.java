package com.ysy.oath.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author mc
 * Create date 2019/7/23 20:07
 * Version 1.0
 * Description
 */
//@Configuration
public class WebSocketConfig {
	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}
}
