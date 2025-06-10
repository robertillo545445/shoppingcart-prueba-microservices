package com.api.rest.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient productWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8081/api/products")
                .build();
    }
}
