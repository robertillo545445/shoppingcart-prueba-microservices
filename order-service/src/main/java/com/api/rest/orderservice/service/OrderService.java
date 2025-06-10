package com.api.rest.orderservice.service;

import com.api.rest.orderservice.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface OrderService {
    Mono<OrderResponseDto> createOrder(OrderRequestDto orderRequest);
    Mono<OrderResponseDto> getOrderById(Long id);
    Flux<OrderResponseDto> getAllOrders();
    Mono<Boolean> isDatabaseUp();



}
