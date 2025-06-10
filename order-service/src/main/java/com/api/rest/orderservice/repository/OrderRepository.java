package com.api.rest.orderservice.repository;


import com.api.rest.orderservice.entity.Order;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import java.util.UUID;

public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {}
