package com.mycompany.leavemanagementsystem;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Locale;

@WebServlet("/app")
public class LeaveServlet extends HttpServlet {
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_EMPLOYEE = "EMPLOYEE";

    private LeaveService leaveService;

    @Override
    public void init() {
        leaveService = new LeaveService(HibernateUtil.initialize());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("logout".equals(action)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("role") == null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        renderDashboard(request, response, null, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        try {
            if ("login".equals(action)) {
                login(request, response);
                return;
            }

            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("role") == null) {
                response.sendRedirect(request.getContextPath() + "/");
                return;
            }
            String role = (String) session.getAttribute("role");
            String username = (String) session.getAttribute("username");
            switch (action == null ? "" : action) {
                case "apply" -> {
                    requireRole(role, ROLE_EMPLOYEE);
                    leaveService.apply(username, parseLeaveType(request.getParameter("leaveType")),
                            parseDate(request.getParameter("startDate")),
                            parseDate(request.getParameter("endDate")), request.getParameter("reason"));
                    redirectWithMessage(request, response, "Leave request submitted.");
                }
                case "cancel" -> {
                    requireRole(role, ROLE_EMPLOYEE);
                    leaveService.cancel(parseRequestId(request), username);
                    redirectWithMessage(request, response, "Pending request cancelled.");
                }
                case "decide" -> {
                    requireRole(role, ROLE_ADMIN);
                    LeaveStatus decision = parseDecision(request.getParameter("decision"));
                    leaveService.decide(parseRequestId(request), decision, request.getParameter("remarks"));
                    redirectWithMessage(request, response,
                            decision == LeaveStatus.APPROVED ? "Request approved." : "Request rejected.");
                }
                default -> throw new IllegalArgumentException("Unknown action.");
            }
        } catch (IllegalArgumentException exception) {
            renderDashboard(request, response, exception.getMessage(), null);
        } catch (RuntimeException exception) {
            getServletContext().log("Unable to process leave request.", exception);
            renderDashboard(request, response,
                    "The request could not be saved. Check the server log and try again.", null);
        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String roleInput = request.getParameter("role");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        boolean admin = ROLE_ADMIN.equals(roleInput)
                && AppConfig.required("ADMIN_USERNAME").equals(username)
                && AppConfig.required("ADMIN_PASSWORD").equals(password);
        boolean employee = ROLE_EMPLOYEE.equals(roleInput)
                && AppConfig.required("EMPLOYEE_USERNAME").equals(username)
                && AppConfig.required("EMPLOYEE_PASSWORD").equals(password);
        if (!admin && !employee) {
            request.setAttribute("error", "The role, username, or password is incorrect.");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }

        HttpSession existing = request.getSession(false);
        if (existing != null) {
            existing.invalidate();
        }
        HttpSession session = request.getSession(true);
        session.setAttribute("role", admin ? ROLE_ADMIN : ROLE_EMPLOYEE);
        session.setAttribute("username", username);
        response.sendRedirect(request.getContextPath() + "/app");
    }

    private void renderDashboard(HttpServletRequest request, HttpServletResponse response,
            String error, String success) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("role") == null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        String role = (String) session.getAttribute("role");
        request.setAttribute("username", session.getAttribute("username"));
        request.setAttribute("success", success != null ? success : session.getAttribute("flash"));
        session.removeAttribute("flash");

        if (ROLE_EMPLOYEE.equals(role)) {
            request.setAttribute("error", error);
            request.setAttribute("today", LocalDate.now());
            request.setAttribute("requests",
                    leaveService.getEmployeeRequests((String) session.getAttribute("username")));
            request.getRequestDispatcher("/WEB-INF/views/employee.jsp").forward(request, response);
            return;
        }

        LeaveStatus filter;
        String dashboardError = error;
        try {
            filter = parseFilter(request.getParameter("status"));
        } catch (IllegalArgumentException exception) {
            filter = null;
            if (dashboardError == null) {
                dashboardError = exception.getMessage();
            }
        }
        request.setAttribute("error", dashboardError);
        request.setAttribute("requests", leaveService.getRequests(filter));
        request.setAttribute("filter", filter == null ? "ALL" : filter.name());
        request.setAttribute("totalRequests", leaveService.countRequests());
        request.setAttribute("pendingRequests", leaveService.countPendingRequests());
        request.getRequestDispatcher("/WEB-INF/views/admin.jsp").forward(request, response);
    }

    private void redirectWithMessage(HttpServletRequest request, HttpServletResponse response,
            String message) throws IOException {
        request.getSession(false).setAttribute("flash", message);
        response.sendRedirect(request.getContextPath() + "/app");
    }

    private static LeaveType parseLeaveType(String value) {
        return Arrays.stream(LeaveType.values())
                .filter(type -> type.name().equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Select a valid leave type."));
    }

    private static LeaveStatus parseDecision(String value) {
        if ("APPROVED".equals(value)) {
            return LeaveStatus.APPROVED;
        }
        if ("REJECTED".equals(value)) {
            return LeaveStatus.REJECTED;
        }
        throw new IllegalArgumentException("Choose approve or reject.");
    }

    private static LeaveStatus parseFilter(String value) {
        if (value == null || value.isBlank() || "ALL".equals(value)) {
            return null;
        }
        try {
            return LeaveStatus.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Select a valid status filter.");
        }
    }

    private static LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException | NullPointerException exception) {
            throw new IllegalArgumentException("Enter valid start and end dates.");
        }
    }

    private static long parseRequestId(HttpServletRequest request) {
        try {
            long id = Long.parseLong(request.getParameter("requestId"));
            if (id < 1) {
                throw new NumberFormatException();
            }
            return id;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Select a valid leave request.");
        }
    }

    private static void requireRole(String actual, String expected) {
        if (!expected.equals(actual)) {
            throw new IllegalArgumentException("You are not allowed to perform that action.");
        }
    }
}
