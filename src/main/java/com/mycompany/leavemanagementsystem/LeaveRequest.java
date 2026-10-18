package com.mycompany.leavemanagementsystem;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_requests")
public class LeaveRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String employeeUsername;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LeaveType leaveType;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false, length = 1000)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LeaveStatus status;

    @Column(length = 1000)
    private String remarks;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    protected LeaveRequest() {
    }

    public LeaveRequest(String employeeUsername, LeaveType leaveType, LocalDate startDate,
            LocalDate endDate, String reason) {
        this.employeeUsername = employeeUsername;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = LeaveStatus.PENDING;
        this.submittedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getEmployeeUsername() {
        return employeeUsername;
    }

    public LeaveType getLeaveType() {
        return leaveType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getReason() {
        return reason;
    }

    public LeaveStatus getStatus() {
        return status;
    }

    public String getRemarks() {
        return remarks;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setStatus(LeaveStatus status) {
        this.status = status;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
