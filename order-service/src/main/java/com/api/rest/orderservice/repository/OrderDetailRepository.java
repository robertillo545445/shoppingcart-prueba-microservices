package com.api.rest.orderservice.repository;


import com.api.rest.orderservice.entity.OrderDetail;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface OrderDetailRepository extends R2dbcRepository<OrderDetail, Long> {

    Flux<OrderDetail> findByOrderId(Long orderId);
}
