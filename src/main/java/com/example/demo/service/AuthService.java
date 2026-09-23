package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.UserResponseDTO;;

@Service
public class AuthService {
    public UserResponseDTO getCurrentUser(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !authentication.isAuthenticated()){

            return null;

        }

        String username = authentication.getName();

        List<String> assignedRoles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        return new UserResponseDTO(username, assignedRoles);
    }
}
