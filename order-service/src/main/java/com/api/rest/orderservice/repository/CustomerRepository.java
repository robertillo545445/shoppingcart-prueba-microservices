package com.api.rest.orderservice.repository;

import com.api.rest.orderservice.entity.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CustomerRepository extends ReactiveCrudRepository<Customer, Long> {}
