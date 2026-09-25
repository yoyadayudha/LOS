package com.example.demo.dto;

import java.math.BigDecimal;

public class LoanResponseDTO {
    private Integer id;
    private String borrowerName;
    private BigDecimal amount;
    private String status;
    private boolean authorizedToApprove;

    public LoanResponseDTO(Integer id, String borrowerName, BigDecimal amount, String status, boolean authorizedToApprove) {
        this.id = id;
        this.borrowerName = borrowerName;
        this.amount = amount;
        this.status = status;
        this.authorizedToApprove = authorizedToApprove;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isAuthorizedToApprove() {
        return authorizedToApprove;
    }

    public void setAuthorizedToApprove(boolean authorizedToApprove) {
        this.authorizedToApprove = authorizedToApprove;
    }

    
    
}
