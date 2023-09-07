package com.example.apigatewayservice.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
//8080에 각 서비스를 호출하면 아래의 필터 설정 내용을 각 서비스에 전송해서 사용가능함.
  @Bean
  public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
      return builder.routes()
              .route(r -> r.path("/first-service/**")
                      .filters(f -> f.addRequestHeader("first-request", "first-request-header")
                              .addResponseHeader("first-response", "first-response-header"))
                      .uri("http://localhost:8081"))
              .route(r -> r.path("/second-service/**")
                      .filters(f -> f.addRequestHeader("second-request", "second-request-header")
                              .addResponseHeader("second-response", "second-response-header"))
                      .uri("http://localhost:8082"))
              .build();
  }
}
