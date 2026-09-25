package com.example.demo.dto;

import com.example.demo.model.Gender;
import com.example.demo.model.EmploymentType;
import com.example.demo.model.ProductType;
import com.example.demo.validation.OnDraft;
import com.example.demo.validation.OnSubmit;
import com.example.demo.model.InterestScheme;
import com.example.demo.model.CollateralType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;


import java.math.BigDecimal;
import java.time.LocalDate;


public class ApplicationFormRequestDTO {
    private Integer loanId;

    @NotBlank(message = "NIK wajib 16 digit angka", groups = {OnDraft.class, OnSubmit.class})
    @Pattern(regexp = "\\d{16}", message = "NIK wajib 16 digit angka", groups = {OnDraft.class, OnSubmit.class})
    private String nik;
    private String fullName;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private Gender gender;
    private String address;

    private String companyName;
    private EmploymentType employmentType;
    private Integer workDurationMonths;
    private BigDecimal monthlyIncome;
    private BigDecimal existingInstallments;

    private ProductType productType;
    private BigDecimal requestedAmount;
    private Integer tenorMonths;
    private InterestScheme interestScheme;
    private String loanPurpose;

    private Boolean hasCollateral;
    private CollateralType collateralType;
    private BigDecimal collateralValue;

    public Integer getLoanId() {
        return loanId;
    }
    public void setLoanId(Integer loanId) {
        this.loanId = loanId;
    }
    public String getNik() {
        return nik;
    }
    public void setNik(String nik) {
        this.nik = nik;
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public LocalDate getBirthDate() {
        return birthDate;
    }
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
    public Gender getGender() {
        return gender;
    }
    public void setGender(Gender gender) {
        this.gender = gender;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
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

    
}
