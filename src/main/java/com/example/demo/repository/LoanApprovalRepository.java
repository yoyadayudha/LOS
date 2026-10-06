package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.LoanApproval;

public interface LoanApprovalRepository extends JpaRepository<LoanApproval, Integer>{
    
}
