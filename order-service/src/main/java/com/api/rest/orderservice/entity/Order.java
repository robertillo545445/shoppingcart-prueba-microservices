package com.api.rest.orderservice.entity;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.MappedCollection;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("orders")
public class Order {
    @Id
    @org.springframework.data.relational.core.mapping.Column("id")
    private Long id;

    private Long customerId; // referencia a Customer

    private Double total;
}
