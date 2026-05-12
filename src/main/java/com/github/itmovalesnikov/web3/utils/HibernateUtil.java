package com.github.itmovalesnikov.web3.utils;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import com.github.itmovalesnikov.web3.model.BPoint;

/**
 * Utility class for managing Hibernate SessionFactory.
 * Initializes the SessionFactory with database connection properties
 * and registers annotated entities.
 */
public class HibernateUtil {
    /** SessionFactory instance for creating database sessions */
    private static final SessionFactory sessionFactory;

    /** Static initializer block to create the SessionFactory */
    static {
        try {
            String dbUsername = System.getenv("DB_USERNAME");
            String dbPassword = System.getenv("DB_PASSWORD");

            if (dbUsername == null || dbPassword == null) {
                throw new RuntimeException("DB_USERNAME or DB_PASSWORD environment variables are not set");
            }
            
            Configuration configuration = new Configuration();
            configuration.setProperty("hibernate.connection.username", dbUsername);
            configuration.setProperty("hibernate.connection.password", dbPassword);
            configuration.setProperty("hibernate.hbm2ddl.auto", "update");
            configuration.setProperty("show_sql", true);
            configuration.addAnnotatedClass(BPoint.class);
            configuration.configure();
            
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();
                
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Returns the singleton SessionFactory instance.
     *
     * @return SessionFactory instance for creating database sessions
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}