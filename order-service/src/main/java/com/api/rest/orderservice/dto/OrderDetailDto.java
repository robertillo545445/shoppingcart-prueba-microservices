package com.api.rest.orderservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailDto {
    private String productId;
    private String productName;
    private Integer quantity;
    private Double price;
}
