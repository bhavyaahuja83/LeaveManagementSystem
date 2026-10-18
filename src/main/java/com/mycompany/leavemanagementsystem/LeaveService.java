package com.mycompany.leavemanagementsystem;

import java.time.LocalDate;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class LeaveService {
    private final SessionFactory sessionFactory;

    public LeaveService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public LeaveRequest apply(String employeeUsername, LeaveType type, LocalDate startDate,
            LocalDate endDate, String reason) {
        if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Leave cannot start in the past.");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("The end date must be on or after the start date.");
        }
        if (reason == null || reason.isBlank() || reason.length() > 1000) {
            throw new IllegalArgumentException("Enter a reason of up to 1000 characters.");
        }

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Long overlapCount = session.createQuery(
                                "select count(l) from LeaveRequest l "
                                + "where l.employeeUsername = :employee "
                                + "and l.status in (:pending, :approved) "
                                + "and l.startDate <= :endDate and l.endDate >= :startDate",
                                Long.class)
                        .setParameter("employee", employeeUsername)
                        .setParameter("pending", LeaveStatus.PENDING)
                        .setParameter("approved", LeaveStatus.APPROVED)
                        .setParameter("startDate", startDate)
                        .setParameter("endDate", endDate)
                        .uniqueResult();
                if (overlapCount != null && overlapCount > 0) {
                    throw new IllegalArgumentException("These dates overlap a pending or approved leave request.");
                }

                LeaveRequest leaveRequest = new LeaveRequest(
                        employeeUsername, type, startDate, endDate, reason.trim());
                session.persist(leaveRequest);
                transaction.commit();
                return leaveRequest;
            } catch (RuntimeException exception) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                throw exception;
            }
        }
    }

    public List<LeaveRequest> getEmployeeRequests(String employeeUsername) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "from LeaveRequest l where l.employeeUsername = :employee "
                            + "order by l.submittedAt desc", LeaveRequest.class)
                    .setParameter("employee", employeeUsername)
                    .list();
        }
    }

    public List<LeaveRequest> getRequests(LeaveStatus filter) {
        try (Session session = sessionFactory.openSession()) {
            if (filter == null) {
                return session.createQuery("from LeaveRequest l order by l.submittedAt desc",
                        LeaveRequest.class).list();
            }
            return session.createQuery(
                            "from LeaveRequest l where l.status = :status order by l.submittedAt desc",
                            LeaveRequest.class)
                    .setParameter("status", filter)
                    .list();
        }
    }

    public long countRequests() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("select count(l) from LeaveRequest l", Long.class).uniqueResult();
        }
    }

    public long countPendingRequests() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "select count(l) from LeaveRequest l where l.status = :status", Long.class)
                    .setParameter("status", LeaveStatus.PENDING)
                    .uniqueResult();
        }
    }

    public void cancel(long requestId, String employeeUsername) {
        updatePendingRequest(requestId, employeeUsername, LeaveStatus.CANCELLED, null);
    }

    public void decide(long requestId, LeaveStatus decision, String remarks) {
        if (decision != LeaveStatus.APPROVED && decision != LeaveStatus.REJECTED) {
            throw new IllegalArgumentException("Choose approve or reject.");
        }
        if (decision == LeaveStatus.REJECTED && (remarks == null || remarks.isBlank())) {
            throw new IllegalArgumentException("Add a remark when rejecting a request.");
        }
        if (remarks != null && remarks.length() > 1000) {
            throw new IllegalArgumentException("Remarks must be 1000 characters or fewer.");
        }
        updatePendingRequest(requestId, null, decision, remarks == null ? null : remarks.trim());
    }

    private void updatePendingRequest(long requestId, String employeeUsername,
            LeaveStatus newStatus, String remarks) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                LeaveRequest request = session.get(LeaveRequest.class, requestId);
                if (request == null
                        || request.getStatus() != LeaveStatus.PENDING
                        || (employeeUsername != null && !employeeUsername.equals(request.getEmployeeUsername()))) {
                    throw new IllegalArgumentException("That pending leave request could not be found.");
                }
                request.setStatus(newStatus);
                request.setRemarks(remarks);
                transaction.commit();
            } catch (RuntimeException exception) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                throw exception;
            }
        }
    }
}
