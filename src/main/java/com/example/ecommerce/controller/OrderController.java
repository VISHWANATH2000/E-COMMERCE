package com.example.ecommerce.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@AuthenticationPrincipal UserDetails user) {
        Order order = orderService.createOrder(user.getUsername());
        return ResponseEntity.ok(new OrderResponse(order));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(@AuthenticationPrincipal UserDetails user) {
        List<Order> orders = orderService.getOrders(user.getUsername());
        List<OrderResponse> responses = orders.stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    public static class OrderResponse {
        private Long id;
        private LocalDateTime createdAt;
        private List<OrderItemResponse> items;
        
        public OrderResponse(Order order) {
            this.id = order.getId();
            this.createdAt = order.getCreatedAt();
            this.items = order.getItems() != null ? order.getItems().stream()
                    .map(OrderItemResponse::new)
                    .collect(Collectors.toList()) : new ArrayList<>();
        }
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public List<OrderItemResponse> getItems() { return items; }
        public void setItems(List<OrderItemResponse> items) { this.items = items; }
    }

    public static class OrderItemResponse {
        private Long id;
        private ProductResponse product;
        private int quantity;
        private double price;
        
        public OrderItemResponse(OrderItem item) {
            this.id = item.getId();
            this.product = item.getProduct() != null ? new ProductResponse(item.getProduct()) : null;
            this.quantity = item.getQuantity();
            this.price = item.getPrice();
        }
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public ProductResponse getProduct() { return product; }
        public void setProduct(ProductResponse product) { this.product = product; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
    }

    public static class ProductResponse {
        private Long id;
        private String name;
        private String description;
        private Double price;
        private String category;
        
        public ProductResponse(Product product) {
            this.id = product.getId();
            this.name = product.getName();
            this.description = product.getDescription();
            this.price = product.getPrice();
            this.category = product.getCategory();
        }
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
    }
} 