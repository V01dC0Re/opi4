package com.github.itmovalesnikov.web3.jmx;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

/**
 * A JMX managed bean that tracks statistics about points in the application.
 * Extends NotificationBroadcasterSupport to support sending notifications when certain events occur.
 * Implements the PointsStatisticsMBean interface to expose metrics via JMX.
 */
public class PointsStatistics extends NotificationBroadcasterSupport implements PointsStatisticsMBean {
    /** Notification type sent when a user makes 4 misses in a row */
    public static final String FOUR_MISSES_NOTIFICATION = "com.github.itmovalesnikov.web3.fourMisses";

    private final AtomicLong totalPoints = new AtomicLong();
    private final AtomicLong missedPoints = new AtomicLong();
    private final AtomicLong sequence = new AtomicLong();
    private final Map<String, Integer> consecutiveMissesByUser = new ConcurrentHashMap<>();

    /**
     * Records a point without specifying a user (uses default user).
     *
     * @param hit whether the point was a hit (true) or a miss (false)
     */
    public void recordPoint(boolean hit) {
        recordPoint("default", hit);
    }

    /**
     * Records a point for a specific user and updates statistics accordingly.
     * If the user has made 4 consecutive misses, a notification is sent.
     *
     * @param userId the ID of the user who made the point
     * @param hit whether the point was a hit (true) or a miss (false)
     */
    public void recordPoint(String userId, boolean hit) {
        long total = totalPoints.incrementAndGet();
        if (hit) {
            consecutiveMissesByUser.put(userId, 0);
            return;
        }

        long missed = missedPoints.incrementAndGet();
        int consecutiveMisses = consecutiveMissesByUser.merge(userId, 1, Integer::sum);
        if (consecutiveMisses == 4) {
            Notification notification = new Notification(
                    FOUR_MISSES_NOTIFICATION,
                    this,
                    sequence.incrementAndGet(),
                    System.currentTimeMillis(),
                    "User " + userId + " made 4 misses in a row. Total points: " + total
                            + ", missed points: " + missed);
            sendNotification(notification);
        }
    }

    @Override
    /**
     * Returns the total number of points recorded.
     *
     * @return the total number of points
     */
    public long getTotalPoints() {
        return totalPoints.get();
    }

    @Override
    /**
     * Returns the total number of missed points recorded.
     *
     * @return the total number of missed points
     */
    public long getMissedPoints() {
        return missedPoints.get();
    }

    @Override
    /**
     * Returns the maximum number of consecutive misses across all users.
     *
     * @return the maximum number of consecutive misses
     */
    public int getConsecutiveMisses() {
        return consecutiveMissesByUser.values().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
    }

    @Override
    /**
     * Resets all statistics to zero.
     */
    public void reset() {
        totalPoints.set(0);
        missedPoints.set(0);
        consecutiveMissesByUser.clear();
    }
}