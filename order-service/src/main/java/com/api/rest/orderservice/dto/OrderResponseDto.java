package com.api.rest.orderservice.dto;

import lombok.*;
import java.util.List;
import java.util.UUID; // Puedes eliminar esta importación si no se usa

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDto {
    private Long orderId;
    private CustomerDto customer; // <-- ¡CAMBIO IMPORTANTE AQUÍ! Ahora es un objeto CustomerDto
    // private Long customer;       // <-- ¡ELIMINA ESTA LÍNEA!
    // private String customerName; // <-- ¡ELIMINA ESTA LÍNEA!
    private List<OrderDetailDto> orderDetails;
    private Double total;
}
