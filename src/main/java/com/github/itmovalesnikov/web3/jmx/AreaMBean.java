package com.github.itmovalesnikov.web3.jmx;

/**
 * Interface for exposing area-related metrics via JMX.
 * Defines methods to retrieve radius and area values.
 */
public interface AreaMBean {
    /**
     * Gets the current radius value.
     *
     * @return the current radius
     */
    double getRadius();

    /**
     * Gets the calculated area based on the current radius.
     *
     * @return the calculated area
     */
    double getArea();
}