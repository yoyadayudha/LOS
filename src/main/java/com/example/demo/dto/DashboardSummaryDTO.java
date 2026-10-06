package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data 
@AllArgsConstructor 
public class DashboardSummaryDTO {
    private long totalSubmitted;
    private long totalInReview;
    private long totalApproved;
    private long totalRejected;
    
}
