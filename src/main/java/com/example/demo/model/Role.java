package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table (name = "role")
public class Role {
    @Id
    private Integer id;

    @Column (name = "role_name", unique = true)
    private String roleName;

    private String description;

    @Column (name = "approval_limit")
    private Long approvalLimit;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getApprovalLimit() {
        return approvalLimit;
    }

    public void setApprovalLimit(Long approvalLimit) {
        this.approvalLimit = approvalLimit;
    }

    
}
