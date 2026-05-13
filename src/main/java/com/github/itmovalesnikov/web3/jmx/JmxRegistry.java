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

/**
 * A JMX registry that manages MBeans for application statistics and metrics.
 * It registers and unregisters MBeans when the servlet context is initialized and destroyed.
 * Provides static methods to interact with registered MBeans from other parts of the application.
 */
public class JmxRegistry implements ServletContextListener {
    private static final PointsStatistics POINTS_STATISTICS = new PointsStatistics();
    private static final Area AREA = new Area();
    private static final String POINTS_STATISTICS_NAME = "com.github.itmovalesnikov.web3:type=PointsStatistics";
    private static final String AREA_NAME = "com.github.itmovalesnikov.web3:type=Area";

    /**
     * Records a point for a specific user and hit status to the points statistics.
     *
     * @param userId the ID of the user who made the point
     * @param hit whether the point was a hit (true) or a miss (false)
     */
    public static void recordPoint(String userId, boolean hit) {
        POINTS_STATISTICS.recordPoint(userId, hit);
    }

    /**
     * Updates the radius value used in area calculations.
     *
     * @param radius the new radius value as a BigFraction
     */
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

    /**
     * Registers an MBean with the given name on the server.
     * If an MBean with the same name is already registered, it will be unregistered first.
     *
     * @param server the MBean server
     * @param name the name to register the MBean under
     * @param mbean the MBean instance
     * @throws InstanceAlreadyExistsException if an MBean with the same name is already registered and cannot be removed
     * @throws MBeanRegistrationException if the registration fails
     * @throws NotCompliantMBeanException if the MBean does not comply with the JMX specification
     */
    private static void register(MBeanServer server, ObjectName name, Object mbean)
            throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
        if (server.isRegistered(name)) {
            unregisterQuietly(server, name.toString());
        }
        server.registerMBean(mbean, name);
    }

    /**
     * Quietly unregisters an MBean from the server, ignoring exceptions.
     * Used during application shutdown.
     *
     * @param server the MBean server
     * @param objectName the name of the MBean to unregister
     */
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