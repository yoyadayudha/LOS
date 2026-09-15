package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/")
    public String ruteUtama(){
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String halamanLogin() {
        return "login";
    }

    @PostMapping("/proses-login")
    public String prosesLogin(
        @RequestParam("inputUdomain") String udomain,
        @RequestParam("inputPassword") String pass,
        HttpSession session
    ){
        
        User userExist = userRepository.findByUdomain(udomain);
        
        if(userExist != null && userExist.getPassword().equals(pass)){
            
            session.setAttribute("userAktif", userExist.getUdomain());
            
            return "redirect:/dashboard";
        }else{
            return "redirect:/login?error=true";
        }
    }
    
    @GetMapping("dashboard")
    public String halamanDashboard(HttpSession session) {
        if(session.getAttribute("userAktif") == null){
            return "redirect:/login";
        }
        
        return "dashboard";
    }

}
