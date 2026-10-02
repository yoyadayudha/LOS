package com.example.demo.dto;

public class DashboardSummaryDTO {
    private long totalSubmitted;
    private long totalInReview;
    private long totalApproved;
    private long totalRejected;
    
    public DashboardSummaryDTO(long totalSubmitted, long totalInReview, long totalApproved, long totalRejected) {
        this.totalSubmitted = totalSubmitted;
        this.totalInReview = totalInReview;
        this.totalApproved = totalApproved;
        this.totalRejected = totalRejected;
    }
    public long getTotalSubmitted() {
        return totalSubmitted;
    }
    public void setTotalSubmitted(long totalSubmitted) {
        this.totalSubmitted = totalSubmitted;
    }
    public long getTotalInReview() {
        return totalInReview;
    }
    public void setTotalInReview(long totalInReview) {
        this.totalInReview = totalInReview;
    }
    public long getTotalApproved() {
        return totalApproved;
    }
    public void setTotalApproved(long totalApproved) {
        this.totalApproved = totalApproved;
    }
    public long getTotalRejected() {
        return totalRejected;
    }
    public void setTotalRejected(long totalRejected) {
        this.totalRejected = totalRejected;
    }


   
}
