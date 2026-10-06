package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

import jakarta.persistence.Column;

@Entity
@Table(name = "users")
@Getter 
@Setter 

public class User {
    @Id
    private Integer id;
    
    @Column (unique = true)
    private String udomain;

    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_role",
        joinColumns = @JoinColumn (name = "user_id"),
        inverseJoinColumns = @JoinColumn (name = "role_id")
    )

    private List<Role> roles;
    
}
