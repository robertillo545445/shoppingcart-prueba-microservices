package com.api.rest.orderservice.service;

import com.api.rest.orderservice.dto.*;
import com.api.rest.orderservice.entity.*;
import com.api.rest.orderservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final WebClient productWebClient;

    private final OrderDetailRepository orderDetailRepository;

    @Autowired
    private DatabaseClient databaseClient;

    public Mono<Boolean> isDatabaseUp() {
        return databaseClient.sql("SELECT 1")
                .fetch()
                .rowsUpdated()
                .map(count -> true)
                .onErrorReturn(false);
    }

    private Mono<ProductDto> getProductById(String productId) {
        return productWebClient.get()
                .uri("/{id}", productId)
                .retrieve()
                .bodyToMono(ProductDto.class);
    }

    @Override
    public Mono<OrderResponseDto> createOrder(OrderRequestDto orderRequest) {
        return customerRepository.findById(Long.valueOf(orderRequest.getCustomerId()))
                .switchIfEmpty(Mono.error(new RuntimeException("Customer not found")))
                .flatMap(customer -> {
                    // 1. Prepara los OrderDetail (sin ID aún, y sin orderId) para calcular el total
                    List<OrderDetail> tempOrderDetails = orderRequest.getOrderDetails().stream()
                            .map(orderDetailDto -> OrderDetail.builder()
                                    .productId(orderDetailDto.getProductId())
                                    .productName(orderDetailDto.getProductName())
                                    .price(orderDetailDto.getPrice())
                                    .quantity(orderDetailDto.getQuantity())
                                    .build())
                            .collect(Collectors.toList());

                    // 2. Calcula el total de la orden
                    double total = tempOrderDetails.stream()
                            .mapToDouble(od -> od.getPrice() * od.getQuantity())
                            .sum();

                    // 3. Construye el objeto Order (SIN la lista de orderDetails adjunta, esto es correcto)
                    Order order = Order.builder()
                            .customerId(customer.getId())
                            .total(total)
                            .build();

                    // 4. Guarda la Order principal para obtener su ID generado
                    return orderRepository.save(order)
                            .flatMap(savedOrder -> {
                                // 5. Una vez que la Order está guardada y tiene un ID,
                                //    asigna ese ID a cada OrderDetail
                                List<OrderDetail> finalOrderDetails = tempOrderDetails.stream()
                                        .map(detail -> {
                                            detail.setOrderId(savedOrder.getId());
                                            return detail;
                                        })
                                        .collect(Collectors.toList());

                                // 6. Guarda todos los OrderDetail
                                return orderDetailRepository.saveAll(finalOrderDetails)
                                        .collectList() // Recoge todos los OrderDetail guardados en una lista
                                        .map(savedDetails -> {
                                            // 7. Pasa savedOrder y savedDetails al método toResponseDto
                                            //    savedOrder.setOrderDetails(savedDetails); // <-- ESTA LÍNEA AHORA SE ELIMINA
                                            return toResponseDto(savedOrder, customer, savedDetails); // <-- PASA LOS DETALLES AQUÍ
                                        });
                            });
                });
    }



    @Override
    public Mono<OrderResponseDto> getOrderById(Long id) {
        return orderRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Order not found with ID: " + id))) // Manejo de error si la orden no existe
                .flatMap(order ->
                        Mono.zip(
                                        // 1. Obtener el cliente
                                        customerRepository.findById(order.getCustomerId())
                                                .switchIfEmpty(Mono.error(new RuntimeException("Customer not found for Order ID: " + order.getId()))), // Manejo de error para el cliente

                                        // 2. Obtener los detalles de la orden y recolectarlos en una lista
                                        orderDetailRepository.findByOrderId(order.getId()).collectList()
                                )
                                // 3. Cuando ambos Mono (Customer y List<OrderDetail>) estén completos, combinarlos
                                .map(tuple -> {
                                    Customer customer = tuple.getT1(); // El primer elemento del zip es el Customer
                                    List<OrderDetail> orderDetails = tuple.getT2(); // El segundo elemento del zip es la List<OrderDetail>
                                    return toResponseDto(order, customer, orderDetails); // Llamar a toResponseDto con los tres argumentos
                                })
                );
    }

    @Override
    public Flux<OrderResponseDto> getAllOrders() {
        return orderRepository.findAll() // Esto devuelve un Flux<Order>
                .flatMap(order ->
                        Mono.zip(
                                        // 1. Obtener el cliente para cada orden
                                        customerRepository.findById(order.getCustomerId())
                                                .switchIfEmpty(Mono.error(new RuntimeException("Customer not found for Order ID: " + order.getId()))), // Manejo de error para el cliente

                                        // 2. Obtener los detalles de la orden y recolectarlos en una lista para cada orden
                                        orderDetailRepository.findByOrderId(order.getId()).collectList()
                                )
                                // 3. Cuando ambos Mono (Customer y List<OrderDetail>) estén completos, combinarlos con la Order original
                                .map(tuple -> {
                                    Customer customer = tuple.getT1(); // El primer elemento del zip es el Customer
                                    List<OrderDetail> orderDetails = tuple.getT2(); // El segundo elemento del zip es la List<OrderDetail>
                                    return toResponseDto(order, customer, orderDetails); // Llamar a toResponseDto con los tres argumentos
                                })
                );
    }

    private OrderResponseDto toResponseDto(Order order, Customer customer, List<OrderDetail> orderDetails) {
        // Aquí es donde se usa la lista 'orderDetails' que recibes como parámetro
        List<OrderDetailDto> orderDetailDtos = orderDetails.stream()
                .map(detail -> OrderDetailDto.builder()
                        .productId(detail.getProductId())
                        .productName(detail.getProductName())
                        .price(detail.getPrice())
                        .quantity(detail.getQuantity())
                        .build())
                .collect(Collectors.toList());

        CustomerDto customerDto = CustomerDto.builder()
                .id(customer.getId())
                .name(customer.getName())
                .build();

        return OrderResponseDto.builder()
                .orderId(order.getId())
                .customer(customerDto)
                .total(order.getTotal())
                .orderDetails(orderDetailDtos) // <-- Aquí se usa la lista `orderDetailDtos` que acabamos de crear
                .build();
    }
}
