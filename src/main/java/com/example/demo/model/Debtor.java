package com.example.demo.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "debtors")
@Getter 
@Setter 
public class Debtor {
    
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nik", length = 16, unique = true, nullable = true)
    private String nik;

    @Column (name = "full_name", length = 100, nullable = true)
    private String fullName;

    @Column (name = "email", length = 100, nullable = true)
    private String email;

    @Column (name = "phone", length = 13, nullable = true)
    private String phone;

    @Column(name = "birth_date", nullable = true)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name ="gender", nullable = true)
    private Gender gender;

    @Column (name = "address", length = 250, nullable = true)
    private String address;

}
