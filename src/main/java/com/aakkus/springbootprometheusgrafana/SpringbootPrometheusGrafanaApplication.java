package com.aakkus.springbootprometheusgrafana;

import lombok.Builder;
import lombok.Data;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootApplication
public class SpringbootPrometheusGrafanaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootPrometheusGrafanaApplication.class, args);
    }

    @Bean
    public CommandLineRunner initializer(OrderService orderService) {
        return args -> {
            OrderItem item = OrderItem.builder().itemName("USB Cable").itemPrice(BigDecimal.TEN).build();

            Order order = Order.builder()
                    .id(1L)
                    .price(BigDecimal.TEN)
                    .orderDate(Instant.now())
                    .items(Collections.singleton(item))
                    .build();

            orderService.save(order);
        };
    }
}

@Data
@Builder
class Order {

    private Long id;
    private Instant orderDate;
    private BigDecimal price;
    private Set<OrderItem> items;
}

@Data
@Builder
class OrderItem {

    private Long id;
    private String itemName;
    private BigDecimal itemPrice;
}

@Builder
@Data
class OrderDto {

    private String date;
    private BigDecimal price;
    private Set<OrderItemDto> orderItems;

}

@Builder
@Data
class OrderItemDto {

    private String name;
    private BigDecimal price;

}

@Service
class OrderService {

    private Map<Long, Order> orders = new HashMap<>();

    Flux<OrderDto> findAll() {
        return Flux.fromIterable(orders.values())
                .map(this::mapOrderToDto);
    }

    private OrderDto mapOrderToDto(Order order) {
        Set<OrderItemDto> itemsDto = order.getItems()
                .stream()
                .map(item -> OrderItemDto.builder().name(item.getItemName()).price(item.getItemPrice()).build())
                .collect(Collectors.toSet());

        return OrderDto.builder()
                .date(order.getOrderDate().toString())
                .price(order.getPrice())
                .orderItems(itemsDto)
                .build();
    }

    void save(Order order) {
        orders.putIfAbsent(order.getId(), order);
    }
}

@RestController
@RequestMapping("/api/v1/orders")
class OrderRestController {

    private final OrderService orderService;

    OrderRestController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public Flux<OrderDto> retrieveAll() {
        return orderService.findAll();
    }
}