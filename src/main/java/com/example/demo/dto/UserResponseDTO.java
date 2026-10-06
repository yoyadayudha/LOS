package com.example.demo.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data 
@AllArgsConstructor 
public class UserResponseDTO {
    private String username;

    private List<String> assignedRoles;

    
}
