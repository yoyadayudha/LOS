package com.example.demo.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data 
@AllArgsConstructor 
public class LoanResponseDTO {
    private Integer id;
    private String applicationNumber;
    private String borrowerName;
    private BigDecimal amount;
    private String status;
    private boolean authorizedToApprove;

}
