package com.api.rest.orderservice.controller;

import com.api.rest.orderservice.dto.*;
import com.api.rest.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;



    @PostMapping
    public Mono<OrderResponseDto> createOrder(@RequestBody OrderRequestDto orderRequest) {
        return orderService.createOrder(orderRequest);
    }

    @GetMapping("/{id}")
    public Mono<OrderResponseDto> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }
    @GetMapping("/all")
    public Flux<OrderResponseDto> getAllOrders() {
        return orderService.isDatabaseUp()
                .flatMapMany(isUp -> {
                    if (!isUp) {
                        return Flux.error(new IllegalStateException("Database not available"));
                    }
                    return orderService.getAllOrders();
                })
                .onErrorResume(error -> {
                    log.error("Error fetching orders", error);
                    return Flux.empty(); // o Flux.error(...)
                });
    }


}
