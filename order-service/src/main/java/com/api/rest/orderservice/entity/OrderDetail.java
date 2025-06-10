package com.api.rest.orderservice.entity;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("order_details")
public class OrderDetail {
    @Id
    @Column("id")
    private Long id;

    @Column("order_id")
    private Long orderId;

    private String productId;
    private String productName;
    private Integer quantity;
    private Double price;
}

