package com.example.apigatewayservice.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class CustomFilter extends AbstractGatewayFilterFactory<CustomFilter.Config> {

	public CustomFilter() {
		super(Config.class);
	}

	 //jwt or auth 설정하면 됨.
	@Override
	public GatewayFilter apply(Config config) {
		// Custom Pre Filter
		return (exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest(); // 비동기 방식
			ServerHttpResponse response = exchange.getResponse();

			log.info("Custom PRE filter: request id -> {}", request.getId());

			// Custom Post Filter
			return chain.filter(exchange).then(Mono.fromRunnable(() -> {//mono , webflux
				log.info("Custom POST filter: response code -> {}", response.getStatusCode());
			}));
		};
	}

	public static class Config {
		// config 정보 넣으면 됨.
	}
}
