<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>My leave | Leave Desk</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css">
</head>
<body>
    <header class="topbar">
        <a class="brand" href="${pageContext.request.contextPath}/app"><span class="brand-mark small">LD</span><span>Leave Desk</span></a>
        <div class="topbar-right"><span class="user-label"><c:out value="${username}"/></span><a class="logout-link" href="${pageContext.request.contextPath}/app?action=logout">Log out</a></div>
    </header>
    <main class="page-wrap">
        <section class="page-heading">
            <div><p class="eyebrow">EMPLOYEE WORKSPACE</p><h1>Your leave, at a glance.</h1><p class="muted">Apply for time off and keep track of every request.</p></div>
            <span class="avatar"><c:out value="${username.substring(0, 1).toUpperCase()}"/></span>
        </section>
        <c:if test="${not empty error}"><div class="alert alert-error"><c:out value="${error}"/></div></c:if>
        <c:if test="${not empty success}"><div class="alert alert-success"><c:out value="${success}"/></div></c:if>
        <div class="employee-grid">
            <section class="panel apply-panel">
                <div class="panel-heading"><div><p class="eyebrow">NEW REQUEST</p><h2>Apply for leave</h2></div><span class="heading-icon">＋</span></div>
                <form action="${pageContext.request.contextPath}/app" method="post" class="form-stack">
                    <input type="hidden" name="action" value="apply">
                    <label for="leaveType">Leave type</label>
                    <select id="leaveType" name="leaveType" required>
                        <option value="">Choose a leave type</option>
                        <option value="CASUAL">Casual Leave</option>
                        <option value="SICK">Sick Leave</option>
                        <option value="PERSONAL">Personal Leave</option>
                    </select>
                    <div class="date-grid">
                        <div><label for="startDate">Start date</label><input id="startDate" type="date" name="startDate" min="${today}" required></div>
                        <div><label for="endDate">End date</label><input id="endDate" type="date" name="endDate" min="${today}" required></div>
                    </div>
                    <label for="reason">Reason</label>
                    <textarea id="reason" name="reason" rows="4" maxlength="1000" placeholder="A short note for your manager" required></textarea>
                    <button class="button button-primary" type="submit">Submit request <span aria-hidden="true">&rarr;</span></button>
                    <p class="form-hint">Requests start as pending. Dates cannot overlap another pending or approved request.</p>
                </form>
            </section>
            <section class="panel history-panel">
                <div class="panel-heading"><div><p class="eyebrow">YOUR RECORD</p><h2>Leave history</h2></div></div>
                <c:choose>
                    <c:when test="${empty requests}">
                        <div class="empty-state"><span class="empty-icon">&#9635;</span><h3>No requests yet</h3><p>Your submitted requests will appear here.</p></div>
                    </c:when>
                    <c:otherwise>
                        <div class="request-list">
                            <c:forEach var="leave" items="${requests}">
                                <article class="request-card">
                                    <div class="request-card-top"><span class="type-label"><c:out value="${leave.leaveType.label}"/></span><span class="status status-${leave.status.name().toLowerCase()}"><c:out value="${leave.status.label}"/></span></div>
                                    <p class="request-dates"><c:out value="${leave.startDate}"/> <span>&mdash;</span> <c:out value="${leave.endDate}"/></p>
                                    <p class="request-reason"><c:out value="${leave.reason}"/></p>
                                    <c:if test="${not empty leave.remarks}"><p class="remarks"><strong>Admin remark:</strong> <c:out value="${leave.remarks}"/></p></c:if>
                                    <c:if test="${leave.status.name() == 'PENDING'}">
                                        <form action="${pageContext.request.contextPath}/app" method="post" class="inline-form">
                                            <input type="hidden" name="action" value="cancel"><input type="hidden" name="requestId" value="${leave.id}">
                                            <button class="button button-quiet" type="submit">Cancel request</button>
                                        </form>
                                    </c:if>
                                </article>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </section>
        </div>
    </main>
</body>
</html>
