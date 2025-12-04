package com.example.demo.service;

import com.example.demo.entity.Cart;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.repo.CartRepository;
import com.example.demo.repo.ProductRepository;
import com.example.demo.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    public void addProductToCart(Long productId, String username) {

        User user = userRepository.findByUsername(username);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // check if product already exists in cart
        List<Cart> existingItems = cartRepository.findByUserUsername(username);

        for (Cart c : existingItems) {
            if (c.getProduct().getId().equals(productId)) {
                c.setQuantity(c.getQuantity() + 1);
                cartRepository.save(c);
                return;
            }
        }

        Cart cart = new Cart();
        cart.setProduct(product);
        cart.setUser(user);
        cart.setQuantity(1);

        cartRepository.save(cart);
    }

    public List<Cart> getUserCart(String username) {
        return cartRepository.findByUserUsername(username);
    }

    public void increaseQuantity(Long cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow();
        cart.setQuantity(cart.getQuantity() + 1);
        cartRepository.save(cart);
    }

    public void decreaseQuantity(Long cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow();
        if (cart.getQuantity() > 1) {
            cart.setQuantity(cart.getQuantity() - 1);
            cartRepository.save(cart);
        } else {
            cartRepository.delete(cart);
        }
    }

    public void removeItem(Long cartId) {
        cartRepository.deleteById(cartId);
    }
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

}
