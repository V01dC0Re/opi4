import com.github.itmovalesnikov.web3.jmx.Area;
import com.github.itmovalesnikov.web3.jmx.PointsStatistics;
import org.apache.commons.math3.fraction.BigFraction;
import org.junit.jupiter.api.Test;

import javax.management.Notification;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JmxBeanTest {
    @Test
    public void pointsStatisticsCountsPointsAndMisses() {
        PointsStatistics statistics = new PointsStatistics();

        statistics.recordPoint(true);
        statistics.recordPoint(false);
        statistics.recordPoint(false);

        assertEquals(3, statistics.getTotalPoints());
        assertEquals(2, statistics.getMissedPoints());
        assertEquals(2, statistics.getConsecutiveMisses());
    }

    @Test
    public void pointsStatisticsSendsNotificationAfterFourMisses() {
        PointsStatistics statistics = new PointsStatistics();
        List<Notification> notifications = new ArrayList<>();
        statistics.addNotificationListener((notification, handback) -> notifications.add(notification), null, null);

        statistics.recordPoint(false);
        statistics.recordPoint(false);
        statistics.recordPoint(false);
        statistics.recordPoint(false);

        assertEquals(1, notifications.size());
        assertEquals(PointsStatistics.FOUR_MISSES_NOTIFICATION, notifications.get(0).getType());
    }

    @Test
    public void areaUsesCurrentRadius() {
        Area area = new Area();
        area.updateRadius(new BigFraction(2));

        assertEquals((12.0 + Math.PI) / 4.0, area.getArea(), 0.000001);
    }
}
