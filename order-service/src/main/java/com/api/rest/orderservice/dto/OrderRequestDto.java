package com.api.rest.orderservice.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDto {
    private String customerId;
    private List<OrderDetailDto> orderDetails;
}
