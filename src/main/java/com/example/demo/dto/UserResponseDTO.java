package com.example.demo.dto;

import java.util.List;

public class UserResponseDTO {
    private String username;

    private List<String> assignedRoles;

    public UserResponseDTO(String username, List<String> assignedRoles) {
        this.username = username;
        this.assignedRoles = assignedRoles;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getAssignedRoles() {
        return assignedRoles;
    }

    public void setAssignedRoles(List<String> assignedRoles) {
        this.assignedRoles = assignedRoles;
    }

    
    
}
