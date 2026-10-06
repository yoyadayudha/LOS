package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Debtor;

public interface DebtorRepository extends JpaRepository<Debtor, Integer>{

    Debtor findByNik(String nik);
    
}
