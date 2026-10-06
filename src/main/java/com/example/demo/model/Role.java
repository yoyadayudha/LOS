package com.example.demo.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table (name = "role")
@Getter 
@Setter 
public class Role {
    @Id
    private Integer id;

    @Column (name = "role_name", unique = true)
    private String roleName;

    private String description;

    @Column (name = "approval_limit")
    private BigDecimal approvalLimit;

    
}
