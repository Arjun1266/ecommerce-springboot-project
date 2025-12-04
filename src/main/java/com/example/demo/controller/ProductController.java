package com.example.demo.controller;

import com.example.demo.entity.Product;
import com.example.demo.repo.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    // 🔥 This mapping opens the product details page
    @GetMapping("/product/{id}")
    public String viewProduct(@PathVariable("id") Long id, Model model, HttpSession session) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product Not Found"));

        model.addAttribute("product", product);
        model.addAttribute("username", session.getAttribute("username"));

        return "product";   // loads product.html
    }
}
