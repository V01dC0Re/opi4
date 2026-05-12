package com.github.itmovalesnikov.web3.jmx;

public interface PointsStatisticsMBean {
    long getTotalPoints();

    long getMissedPoints();

    int getConsecutiveMisses();

    void reset();
}
