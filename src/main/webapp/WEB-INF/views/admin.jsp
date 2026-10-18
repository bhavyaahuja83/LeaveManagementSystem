<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Requests | Leave Desk</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css">
</head>
<body>
    <header class="topbar">
        <a class="brand" href="${pageContext.request.contextPath}/app"><span class="brand-mark small">LD</span><span>Leave Desk</span></a>
        <div class="topbar-right"><span class="user-label"><c:out value="${username}"/> &middot; Admin</span><a class="logout-link" href="${pageContext.request.contextPath}/app?action=logout">Log out</a></div>
    </header>
    <main class="page-wrap">
        <section class="page-heading">
            <div><p class="eyebrow">ADMIN WORKSPACE</p><h1>Leave requests.</h1><p class="muted">Review submissions and keep the team moving.</p></div>
            <span class="avatar admin-avatar">AD</span>
        </section>
        <c:if test="${not empty error}"><div class="alert alert-error"><c:out value="${error}"/></div></c:if>
        <c:if test="${not empty success}"><div class="alert alert-success"><c:out value="${success}"/></div></c:if>
        <section class="stats-grid">
            <article class="stat-card"><span class="stat-icon stat-icon-blue">&#9635;</span><span class="stat-label">Total requests</span><strong><c:out value="${totalRequests}"/></strong><span class="stat-caption">All time</span></article>
            <article class="stat-card"><span class="stat-icon stat-icon-amber">&#9719;</span><span class="stat-label">Pending review</span><strong><c:out value="${pendingRequests}"/></strong><span class="stat-caption">Needs your attention</span></article>
        </section>
        <section class="panel admin-list-panel">
            <div class="panel-heading list-heading"><div><p class="eyebrow">TEAM OVERVIEW</p><h2>Employee leave history</h2></div>
                <form action="${pageContext.request.contextPath}/app" method="get" class="filter-form">
                    <label class="sr-only" for="status">Filter by status</label>
                    <select id="status" name="status" onchange="this.form.submit()">
                        <option value="ALL" ${filter == 'ALL' ? 'selected' : ''}>All statuses</option>
                        <option value="PENDING" ${filter == 'PENDING' ? 'selected' : ''}>Pending</option>
                        <option value="APPROVED" ${filter == 'APPROVED' ? 'selected' : ''}>Approved</option>
                        <option value="REJECTED" ${filter == 'REJECTED' ? 'selected' : ''}>Rejected</option>
                        <option value="CANCELLED" ${filter == 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                    </select>
                </form>
            </div>
            <c:choose>
                <c:when test="${empty requests}">
                    <div class="empty-state"><span class="empty-icon">&#9635;</span><h3>No matching requests</h3><p>Try another status filter or check again later.</p></div>
                </c:when>
                <c:otherwise>
                    <div class="table-scroll"><table class="request-table">
                        <thead><tr><th>Employee</th><th>Leave type</th><th>Dates</th><th>Reason</th><th>Status</th><th>Action</th></tr></thead>
                        <tbody><c:forEach var="leave" items="${requests}">
                            <tr>
                                <td><span class="employee-name"><c:out value="${leave.employeeUsername}"/></span><span class="table-subtext">Submitted <c:out value="${leave.submittedAt.toLocalDate()}"/></span></td>
                                <td><c:out value="${leave.leaveType.label}"/></td>
                                <td class="date-cell"><c:out value="${leave.startDate}"/><span>to</span><c:out value="${leave.endDate}"/></td>
                                <td class="reason-cell"><c:out value="${leave.reason}"/>
                                    <c:if test="${not empty leave.remarks}"><span class="table-subtext">Remark: <c:out value="${leave.remarks}"/></span></c:if>
                                </td>
                                <td><span class="status status-${leave.status.name().toLowerCase()}"><c:out value="${leave.status.label}"/></span></td>
                                <td class="action-cell">
                                    <c:if test="${leave.status.name() == 'PENDING'}">
                                        <form action="${pageContext.request.contextPath}/app" method="post" class="decision-form">
                                            <input type="hidden" name="action" value="decide"><input type="hidden" name="requestId" value="${leave.id}">
                                            <input type="text" name="remarks" maxlength="1000" placeholder="Remark if rejecting" aria-label="Remark for rejection">
                                            <div><button class="button button-approve" type="submit" name="decision" value="APPROVED">Approve</button><button class="button button-reject" type="submit" name="decision" value="REJECTED">Reject</button></div>
                                        </form>
                                    </c:if>
                                    <c:if test="${leave.status.name() != 'PENDING'}"><span class="muted small-copy">No action available</span></c:if>
                                </td>
                            </tr>
                        </c:forEach></tbody>
                    </table></div>
                </c:otherwise>
            </c:choose>
        </section>
    </main>
</body>
</html>
