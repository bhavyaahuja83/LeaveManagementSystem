package com.mycompany.leavemanagementsystem;

public enum LeaveType {
    CASUAL("Casual Leave"),
    SICK("Sick Leave"),
    PERSONAL("Personal Leave");

    private final String label;

    LeaveType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
