package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.dto.UserResponseDTO;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthService;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
    
    @Autowired 
    private AuthService authService;

    @GetMapping("/")
    public String ruteUtama(){
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String halamanLogin() {
        return "login";
    }

    @GetMapping("/applications/dashboard")
    public String halamanDashboard(Model model) {
        
        UserResponseDTO currentUser = authService.getCurrentUser();

        model.addAttribute("currentUser", currentUser);

        return "dashboard";
    }
    

}
