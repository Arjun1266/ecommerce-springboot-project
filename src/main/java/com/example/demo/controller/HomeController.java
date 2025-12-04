package com.example.demo.controller;

import com.example.demo.repo.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/home")
    public String home(Model model, HttpSession session) {

        String username = (String) session.getAttribute("username");

        model.addAttribute("username", username);
        model.addAttribute("products", productRepository.findAll());

        return "home";
    }
}
