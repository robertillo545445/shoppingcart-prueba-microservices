package com.api.rest.orderservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("customers")
public class Customer {
    @Id
    @org.springframework.data.relational.core.mapping.Column("id")
    private Long id;
    private String name;
    private String email;
}