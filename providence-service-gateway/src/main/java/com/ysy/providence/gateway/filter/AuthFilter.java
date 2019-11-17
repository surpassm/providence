package com.ysy.providence.gateway.filter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * @author mc
 * version 1.0v
 * date 2019/6/23 13:12
 * description 网管拦截器
 */

@Component
@ConditionalOnProperty(value = "gateway.log.enable", matchIfMissing = true)
public class AuthFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest originalRequest = exchange.getRequest();
		URI originalRequestUrl = originalRequest.getURI();

		//只记录http的请求
		String scheme = originalRequestUrl.getScheme();
		if ((!"http" .equals(scheme) && !"https" .equals(scheme))) {
			return chain.filter(exchange);
		}

		String upgrade = originalRequest.getHeaders().getUpgrade();
		if ("websocket" .equalsIgnoreCase(upgrade)) {
			return chain.filter(exchange);
		}
		return chain.filter(exchange);
	}
	@Override
	public int getOrder() {
		return 0;
	}
}
