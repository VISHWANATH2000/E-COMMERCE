package com.example.ecommerce.service;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    public Cart getCartByUser(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return null;
        Cart cart = cartRepository.findByUser(user).orElse(null);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cartRepository.save(cart);
        }
        return cart;
    }

    public Cart addToCart(String username, Long productId, int quantity) {
        Cart cart = getCartByUser(username);
        Product product = productRepository.findById(productId).orElse(null);
        if (cart == null || product == null) return null;
        List<CartItem> items = cart.getItems();
        if (items == null) {
            items = new ArrayList<>();
            cart.setItems(items);
        }
        CartItem found = null;
        for (CartItem item : items) {
            if (item.getProduct().getId().equals(productId)) {
                found = item;
                break;
            }
        }
        if (found != null) {
            found.setQuantity(found.getQuantity() + quantity);
            cartItemRepository.save(found);
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(quantity);
            cartItemRepository.save(item);
            items.add(item);
        }
        cartRepository.save(cart);
        return cart;
    }

    public Cart updateCartItem(String username, Long itemId, int quantity) {
        Cart cart = getCartByUser(username);
        CartItem item = cartItemRepository.findById(itemId).orElse(null);
        if (cart == null || item == null) return null;
        if (!item.getCart().getId().equals(cart.getId())) return null;
        item.setQuantity(quantity);
        cartItemRepository.save(item);
        cartRepository.save(cart);
        return cart;
    }

    public Cart removeCartItem(String username, Long itemId) {
        Cart cart = getCartByUser(username);
        CartItem item = cartItemRepository.findById(itemId).orElse(null);
        if (cart == null || item == null) return null;
        if (!item.getCart().getId().equals(cart.getId())) return null;
        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        cartRepository.save(cart);
        return cart;
    }

    public void clearCart(String username) {
        Cart cart = getCartByUser(username);
        if (cart == null) return;
        List<CartItem> items = cart.getItems();
        if (items != null) {
            cartItemRepository.deleteAll(items);
            items.clear();
            cartRepository.save(cart);
        }
    }
} 