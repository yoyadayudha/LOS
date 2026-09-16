package com.example.demo.dto;

public class LoanResponseDTO {
    private Integer id;
    private String borrowerName;
    private Long amount;
    private String status;
    private boolean authorizedToApprove;

    public LoanResponseDTO(Integer id, String borrowerName, Long amount, String status, boolean authorizedToApprove) {
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

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
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
