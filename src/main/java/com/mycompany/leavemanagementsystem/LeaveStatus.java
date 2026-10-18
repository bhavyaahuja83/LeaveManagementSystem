package com.mycompany.leavemanagementsystem;

public enum LeaveStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    CANCELLED("Cancelled");

    private final String label;

    LeaveStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
