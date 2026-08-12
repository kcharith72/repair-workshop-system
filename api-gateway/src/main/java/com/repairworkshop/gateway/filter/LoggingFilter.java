package com.repairworkshop.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        logger.info("========== GATEWAY REQUEST ==========");
        logger.info("Timestamp  : {}", LocalDateTime.now());
        logger.info("Method     : {}", request.getMethod());
        logger.info("Path       : {}", request.getPath());
        logger.info("Request ID : {}", request.getId());
        logger.info("=====================================");

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            logger.info("========== GATEWAY RESPONSE ==========");
            logger.info("Status     : {}", response.getStatusCode());
            logger.info("Path       : {}", request.getPath());
            logger.info("======================================");
        }));
    }

    @Override
    public int getOrder() {
        return -1; // Run before all other filters
    }
}
