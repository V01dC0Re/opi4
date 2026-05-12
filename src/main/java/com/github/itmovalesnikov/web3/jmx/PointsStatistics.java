package com.github.itmovalesnikov.web3.jmx;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

public class PointsStatistics extends NotificationBroadcasterSupport implements PointsStatisticsMBean {
    public static final String FOUR_MISSES_NOTIFICATION = "com.github.itmovalesnikov.web3.fourMisses";

    private final AtomicLong totalPoints = new AtomicLong();
    private final AtomicLong missedPoints = new AtomicLong();
    private final AtomicLong sequence = new AtomicLong();
    private final Map<String, Integer> consecutiveMissesByUser = new ConcurrentHashMap<>();

    public void recordPoint(boolean hit) {
        recordPoint("default", hit);
    }

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
    public long getTotalPoints() {
        return totalPoints.get();
    }

    @Override
    public long getMissedPoints() {
        return missedPoints.get();
    }

    @Override
    public int getConsecutiveMisses() {
        return consecutiveMissesByUser.values().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
    }

    @Override
    public void reset() {
        totalPoints.set(0);
        missedPoints.set(0);
        consecutiveMissesByUser.clear();
    }
}
