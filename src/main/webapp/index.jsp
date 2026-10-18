<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Leave Desk | Sign in</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css">
</head>
<body class="login-page">
    <main class="login-shell">
        <section class="login-intro">
            <div class="brand-mark">LD</div>
            <p class="eyebrow">LEAVE DESK</p>
            <h1>Time away,<br>made simple.</h1>
            <p class="intro-copy">A clear place to plan, request, and manage time off.</p>
            <div class="intro-note"><span class="note-dot"></span> Employee and admin access</div>
        </section>
        <section class="login-card">
            <p class="eyebrow">WELCOME BACK</p>
            <h2>Sign in to your account</h2>
            <p class="muted">Use your assigned demo account to continue.</p>
            <c:if test="${not empty error}"><div class="alert alert-error"><c:out value="${error}"/></div></c:if>
            <form action="${pageContext.request.contextPath}/app" method="post" class="form-stack">
                <input type="hidden" name="action" value="login">
                <label for="role">Account type</label>
                <select id="role" name="role" required>
                    <option value="EMPLOYEE">Employee</option>
                    <option value="ADMIN">Administrator</option>
                </select>
                <label for="username">Username</label>
                <input id="username" name="username" autocomplete="username" required>
                <label for="password">Password</label>
                <input id="password" type="password" name="password" autocomplete="current-password" required>
                <button class="button button-primary button-full" type="submit">Sign in <span aria-hidden="true">&rarr;</span></button>
            </form>
            <p class="login-footnote">Demo credentials are configured by the server administrator.</p>
        </section>
    </main>
</body>
</html>
