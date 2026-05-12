package com.github.itmovalesnikov.web3.jmx;

import java.lang.management.ManagementFactory;

import org.apache.commons.math3.fraction.BigFraction;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class JmxRegistry implements ServletContextListener {
    private static final PointsStatistics POINTS_STATISTICS = new PointsStatistics();
    private static final Area AREA = new Area();
    private static final String POINTS_STATISTICS_NAME = "com.github.itmovalesnikov.web3:type=PointsStatistics";
    private static final String AREA_NAME = "com.github.itmovalesnikov.web3:type=Area";

    public static void recordPoint(String userId, boolean hit) {
        POINTS_STATISTICS.recordPoint(userId, hit);
    }

    public static void updateRadius(BigFraction radius) {
        AREA.updateRadius(radius);
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            register(server, new ObjectName(POINTS_STATISTICS_NAME), POINTS_STATISTICS);
            register(server, new ObjectName(AREA_NAME), AREA);
        } catch (MalformedObjectNameException | InstanceAlreadyExistsException
                 | MBeanRegistrationException | NotCompliantMBeanException e) {
            throw new IllegalStateException("Cannot register application MBeans", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        unregisterQuietly(server, POINTS_STATISTICS_NAME);
        unregisterQuietly(server, AREA_NAME);
    }

    private static void register(MBeanServer server, ObjectName name, Object mbean)
            throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
        if (server.isRegistered(name)) {
            unregisterQuietly(server, name.toString());
        }
        server.registerMBean(mbean, name);
    }

    private static void unregisterQuietly(MBeanServer server, String objectName) {
        try {
            ObjectName name = new ObjectName(objectName);
            if (server.isRegistered(name)) {
                server.unregisterMBean(name);
            }
        } catch (Exception ignored) {
            // Web containers may call shutdown hooks after the platform server starts tearing down.
        }
    }
}
