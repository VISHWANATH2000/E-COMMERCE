package com.example.ecommerce.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal UserDetails user) {
        Cart cart = cartService.getCartByUser(user.getUsername());
        return ResponseEntity.ok(new CartResponse(cart));
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(@AuthenticationPrincipal UserDetails user, @RequestBody AddToCartRequest req) {
        Cart cart = cartService.addToCart(user.getUsername(), req.getProductId(), req.getQuantity());
        return ResponseEntity.ok(new CartResponse(cart));
    }

    @PutMapping("/item/{itemId}")
    public ResponseEntity<CartResponse> updateCartItem(@AuthenticationPrincipal UserDetails user, @PathVariable Long itemId, @RequestBody UpdateCartItemRequest req) {
        Cart cart = cartService.updateCartItem(user.getUsername(), itemId, req.getQuantity());
        return ResponseEntity.ok(new CartResponse(cart));
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<CartResponse> removeCartItem(@AuthenticationPrincipal UserDetails user, @PathVariable Long itemId) {
        Cart cart = cartService.removeCartItem(user.getUsername(), itemId);
        return ResponseEntity.ok(new CartResponse(cart));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(@AuthenticationPrincipal UserDetails user) {
        cartService.clearCart(user.getUsername());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> test() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Cart API is working");
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    public static class AddToCartRequest {
        private Long productId;
        private int quantity;
        public AddToCartRequest() {}
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    public static class UpdateCartItemRequest {
        private int quantity;
        public UpdateCartItemRequest() {}
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    public static class CartResponse {
        private Long id;
        private List<CartItemResponse> items;
        
        public CartResponse(Cart cart) {
            this.id = cart.getId();
            this.items = cart.getItems() != null ? cart.getItems().stream()
                    .map(CartItemResponse::new)
                    .collect(Collectors.toList()) : new ArrayList<>();
        }
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public List<CartItemResponse> getItems() { return items; }
        public void setItems(List<CartItemResponse> items) { this.items = items; }
    }

    public static class CartItemResponse {
        private Long id;
        private ProductResponse product;
        private int quantity;
        
        public CartItemResponse(CartItem item) {
            this.id = item.getId();
            this.product = item.getProduct() != null ? new ProductResponse(item.getProduct()) : null;
            this.quantity = item.getQuantity();
        }
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public ProductResponse getProduct() { return product; }
        public void setProduct(ProductResponse product) { this.product = product; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
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