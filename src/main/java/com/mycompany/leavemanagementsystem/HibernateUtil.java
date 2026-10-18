package com.mycompany.leavemanagementsystem;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public final class HibernateUtil {
    private static SessionFactory sessionFactory;
    private static StandardServiceRegistry serviceRegistry;

    private HibernateUtil() {
    }

    public static synchronized SessionFactory initialize() {
        if (sessionFactory != null) {
            return sessionFactory;
        }

        serviceRegistry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver")
                .applySetting("hibernate.connection.url", AppConfig.get("DB_URL",
                        "jdbc:mysql://localhost:3306/leave_management?createDatabaseIfNotExist=true&serverTimezone=UTC"))
                .applySetting("hibernate.connection.username", AppConfig.get("DB_USERNAME", "root"))
                .applySetting("hibernate.connection.password", AppConfig.get("DB_PASSWORD", ""))
                .applySetting("hibernate.dialect", "org.hibernate.dialect.MySQLDialect")
                .applySetting("hibernate.hbm2ddl.auto", "update")
                .applySetting("hibernate.show_sql", "false")
                .build();
        try {
            sessionFactory = new MetadataSources(serviceRegistry)
                    .addAnnotatedClass(LeaveRequest.class)
                    .buildMetadata()
                    .buildSessionFactory();
            return sessionFactory;
        } catch (RuntimeException exception) {
            StandardServiceRegistryBuilder.destroy(serviceRegistry);
            serviceRegistry = null;
            throw exception;
        }
    }

    public static synchronized void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            sessionFactory = null;
        }
        if (serviceRegistry != null) {
            StandardServiceRegistryBuilder.destroy(serviceRegistry);
            serviceRegistry = null;
        }
    }
}
