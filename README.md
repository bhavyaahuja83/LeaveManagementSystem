# Leave Management System

A small server-rendered leave management demo built with Jakarta Servlets, JSP, Hibernate, and MySQL. Credentials are checked by the Java server; they are not shipped in browser JavaScript.

## Configure MySQL and demo users

Edit the project-root `.env` file before starting the server:

```properties
DB_URL=jdbc:mysql://localhost:3306/leave_management?createDatabaseIfNotExist=true&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=your-local-mysql-password
ADMIN_USERNAME=admin
ADMIN_PASSWORD=admin123
EMPLOYEE_USERNAME=employee
EMPLOYEE_PASSWORD=employee123
```

The provided `.env` is ignored by Git. Hibernate creates or updates the `leave_requests` table on startup. The JDBC URL asks MySQL to create the `leave_management` database if it does not exist; the MySQL account must have permission to do so. For a blank local root password, leave `DB_PASSWORD` empty.

To create the database and table yourself, run [`database/schema.sql`](./database/schema.sql) in MySQL Workbench or the MySQL command-line client. The script is safe to run again and matches the Hibernate entity mapping.

Environment variables take precedence over values in `.env`. The app checks the deployed application directory and its parent directories for `.env`, which supports NetBeans in-place deployment from this project. If deploying the WAR elsewhere, set these values as process environment variables or make the project-root `.env` available to the Tomcat process; do not package `.env` into the WAR.

## Run

Use Java 17, Maven, and a Jakarta Servlet 6-compatible container such as Tomcat 10.1. Build the WAR with:

```sh
mvn clean package
```

Deploy `target/LeaveManagementSystem-1.0-SNAPSHOT.war` to Tomcat. Open the deployed application context in a browser. Select Employee or Administrator at sign in and use the corresponding values from `.env`.

## MVP behavior

- Employees can submit Casual, Sick, or Personal leave requests, see their history, and cancel pending requests.
- Requests cannot start in the past, have an end date before their start date, or overlap another pending/approved request for that employee.
- Administrators can filter the full employee leave history, view total and pending request counts, and approve or reject pending requests. A rejection requires a remark.
- Demo usernames and passwords are not intended for production use. This app intentionally has no registration, password reset, OTP, or external authentication provider.
