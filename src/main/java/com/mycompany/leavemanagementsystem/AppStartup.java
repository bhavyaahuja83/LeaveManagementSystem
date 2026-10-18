package com.mycompany.leavemanagementsystem;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppStartup implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            AppConfig.loadFromApplicationPath(event.getServletContext().getRealPath("/"));
            AppConfig.required("ADMIN_USERNAME");
            AppConfig.required("ADMIN_PASSWORD");
            AppConfig.required("EMPLOYEE_USERNAME");
            AppConfig.required("EMPLOYEE_PASSWORD");
            HibernateUtil.initialize();
        } catch (RuntimeException exception) {
            event.getServletContext().log("Unable to start Leave Management System.", exception);
            throw exception;
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        HibernateUtil.shutdown();
    }
}
