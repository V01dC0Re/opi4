package com.github.itmovalesnikov.web3.jmx;

import org.apache.commons.math3.fraction.BigFraction;

public class Area implements AreaMBean {
    private volatile double radius = 1.0;

    public void updateRadius(BigFraction radius) {
        this.radius = radius.doubleValue();
    }

    @Override
    public double getRadius() {
        return radius;
    }

    @Override
    public double getArea() {
        return (12.0 + Math.PI) * radius * radius / 16.0;
    }
}
