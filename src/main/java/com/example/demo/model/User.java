package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "users")

public class User {
    @Id
    private Integer id;
    
    private String udomain;
    private String password;
    private String role;
    
    @Column(name = "approval_limit")
    private Long approvalLimit;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUdomain() {
        return udomain;
    }

    public void setUdomain(String udomain) {
        this.udomain = udomain;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getApprovalLimit() {
        return approvalLimit;
    }

    public void setApprovalLimit(Long approvalLimit) {
        this.approvalLimit = approvalLimit;
    }

    
}
