package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import java.math.BigDecimal;

@Entity
@Table(name = "loans")
public class Loan {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "debtor_nik", referencedColumnName = "nik")
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


    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }

    public Integer getWorkDurationMonths() {
        return workDurationMonths;
    }

    public void setWorkDurationMonths(Integer workDurationMonths) {
        this.workDurationMonths = workDurationMonths;
    }

    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(BigDecimal monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public BigDecimal getExistingInstallments() {
        return existingInstallments;
    }

    public void setExistingInstallments(BigDecimal existingInstallments) {
        this.existingInstallments = existingInstallments;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(BigDecimal requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public Integer getTenorMonths() {
        return tenorMonths;
    }

    public void setTenorMonths(Integer tenorMonths) {
        this.tenorMonths = tenorMonths;
    }

    public InterestScheme getInterestScheme() {
        return interestScheme;
    }

    public void setInterestScheme(InterestScheme interestScheme) {
        this.interestScheme = interestScheme;
    }

    public String getLoanPurpose() {
        return loanPurpose;
    }

    public void setLoanPurpose(String loanPurpose) {
        this.loanPurpose = loanPurpose;
    }

    public Boolean getHasCollateral() {
        return hasCollateral;
    }

    public void setHasCollateral(Boolean hasCollateral) {
        this.hasCollateral = hasCollateral;
    }

    public CollateralType getCollateralType() {
        return collateralType;
    }

    public void setCollateralType(CollateralType collateralType) {
        this.collateralType = collateralType;
    }

    public BigDecimal getCollateralValue() {
        return collateralValue;
    }

    public void setCollateralValue(BigDecimal collateralValue) {
        this.collateralValue = collateralValue;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ApplicationStatus status = ApplicationStatus.DRAFT;

    @Column(name = "created_by")
    private Integer createdBy;

    public Debtor getDebtor() {
        return debtor;
    }

    public void setDebtor(Debtor debtor) {
        this.debtor = debtor;
    }


}
