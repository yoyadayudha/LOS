package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.service.AuthService;

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
    

}
