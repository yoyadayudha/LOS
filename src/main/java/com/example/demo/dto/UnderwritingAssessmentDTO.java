package com.example.demo.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data 
@AllArgsConstructor 
public class UnderwritingAssessmentDTO {
    private BigDecimal anew;
    private BigDecimal dsr;
    private BigDecimal ltv;
    private String recommendation;
}
