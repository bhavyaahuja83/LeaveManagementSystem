CREATE DATABASE IF NOT EXISTS leave_management
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE leave_management;

CREATE TABLE IF NOT EXISTS leave_requests (
    id BIGINT NOT NULL AUTO_INCREMENT,
    employee_username VARCHAR(80) NOT NULL,
    leave_type VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reason VARCHAR(1000) NOT NULL,
    request_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    remarks VARCHAR(1000) NULL,
    submitted_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_leave_employee_status_dates
        (employee_username, request_status, start_date, end_date),
    INDEX idx_leave_submitted_at (submitted_at)
) ENGINE=InnoDB;
