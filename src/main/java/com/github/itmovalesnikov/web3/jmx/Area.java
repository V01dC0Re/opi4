package com.github.itmovalesnikov.web3.jmx;

import org.apache.commons.math3.fraction.BigFraction;

/**
 * A JMX managed bean that calculates the area of a specific geometric shape based on radius.
 * Implements the AreaMBean interface to expose metrics via JMX.
 */
public class Area implements AreaMBean {
    private volatile double radius = 1.0;

    /**
     * Updates the radius value used in area calculations.
     *
     * @param radius the new radius value as a BigFraction
     */
    public void updateRadius(BigFraction radius) {
        this.radius = radius.doubleValue();
    }

    @Override
    /**
     * Returns the current radius value.
     *
     * @return the current radius
     */
    public double getRadius() {
        return radius;
    }

    @Override
    /**
     * Calculates and returns the area of the geometric shape.
     * The formula used is (12.0 + π) * radius² / 16.0
     *
     * @return the calculated area
     */
    public double getArea() {
        return (12.0 + Math.PI) * radius * radius / 16.0;
    }
}