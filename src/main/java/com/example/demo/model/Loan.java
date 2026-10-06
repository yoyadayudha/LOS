package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import java.math.BigDecimal;

@Entity
@Table(name = "loans")
@Getter 
@Setter 
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column (name = "application_number", unique = true)
    private String applicationNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "debtor_id", referencedColumnName = "id")
    private Debtor debtor;

    @Column (name = "company_name")
    private String companyName;

    @Enumerated (EnumType.STRING)
    @Column (name = "employment_type")
    private EmploymentType employmentType;

    @Column (name = "work_duration_months")
    private Integer workDurationMonths;

    @Column (name = "monthly_income", precision = 15, scale = 2)
    private BigDecimal monthlyIncome;

    @Column (name = "existing_installments", precision = 15, scale = 2)
    private BigDecimal existingInstallments;

    @Enumerated (EnumType.STRING)
    @Column (name = "product_type")
    private ProductType productType;
    
    @Column (name = "requested_amount", precision = 15, scale = 2)
    private BigDecimal requestedAmount;

    @Column (name = "tenor_months")
    private Integer tenorMonths;

    @Enumerated (EnumType.STRING)
    @Column (name = "interest_scheme")
    private InterestScheme interestScheme;

    @Column (name = "loan_purpose", length = 150)
    private String loanPurpose;

    @Column (name ="has_collateral")
    private Boolean hasCollateral;

    @Enumerated (EnumType.STRING)
    @Column (name = "collateral_type")
    private CollateralType collateralType;

    @Column (name = "collateral_value", precision = 15, scale = 2)
    private BigDecimal collateralValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ApplicationStatus status = ApplicationStatus.DRAFT;

    @Column(name = "created_by")
    private Integer createdBy;


}
