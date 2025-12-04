package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.User;
import com.example.demo.repo.UserRepository;

@Controller	
public class SignupController {
    
    private final UserRepository repo;
    
    public SignupController(UserRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/signup")
    public String userSignup() {
        return "signup"; // signup.html
    }
    
    @PostMapping("/signup")
    public String registerUser(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               @RequestParam("phoneno") Long phoneno,
                               @RequestParam("email") String email,
                               Model model) {

        if (repo.findByUsername(username) != null) {
            model.addAttribute("error", "Username already exists");
            return "signup";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setPhoneno(phoneno);
        user.setemail(email);
        repo.save(user);

        model.addAttribute("success", "Signup successful! Please login.");
        return "login";
    }

}
