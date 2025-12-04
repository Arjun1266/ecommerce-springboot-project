package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.User;
import com.example.demo.repo.UserRepository;

@Controller
public class EcomController { 

    private final UserRepository repo;

    public EcomController(UserRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/")
    public String index() {
        return "index"; 
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login"; 
    }

    @PostMapping("/login")
    public String verifyLogin(@RequestParam("username") String username,
                              @RequestParam("password") String password,
                              Model model,
                              HttpSession session) {

        User user = repo.findByUsername(username);

        if (user != null && user.getPassword().equals(password)) {

            session.setAttribute("username", username);  // store login info
            return "redirect:/home";
        }

        model.addAttribute("error", "Invalid username or password!");
        return "login";
    }

    // LOGOUT
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
