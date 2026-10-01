package com.example.demo.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.ApplicationStatus;
import com.example.demo.model.Loan;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Integer>{
    List<Loan> findByCreatedByAndStatusIn(Integer createdBy, List<ApplicationStatus> statuses);

    List<Loan> findByStatus(ApplicationStatus status);

    long countByStatus(ApplicationStatus status);
    
    @Query ("SELECT l FROM Loan l WHERE l.status = :status AND l.requestedAmount <= :limit")
    List <Loan> findApproverEligibleTasks(@Param("status") ApplicationStatus status, @Param("limit") BigDecimal limit);
}
