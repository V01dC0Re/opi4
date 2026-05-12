import com.github.itmovalesnikov.web3.model.BPoint;
import org.apache.commons.math3.fraction.BigFraction;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BPointTest {

    @Test
    public void testBPointConstructorAndGetterMethods() {
        BigFraction x = new BigFraction(1, 2);
        BigFraction y = new BigFraction(1, 4);
        BigFraction r = new BigFraction(2, 1);
        String sessionId = "testSession";

        BPoint point = new BPoint(x, y, r, sessionId);

        assertEquals("0.5", point.getX());
        assertEquals("0.25", point.getY());
        assertEquals("2", point.getR());
        assertEquals(sessionId, point.getSessionId());
    }

    @Test
    public void testBPointHitInSecondQuarter() {
        // Point in second quarter: x < 0, y > 0, satisfying y-x*2 <= 1
        BigFraction x = new BigFraction(-1, 4); // -0.25
        BigFraction y = new BigFraction(1, 2); // 0.5
        BigFraction r = new BigFraction(1, 1); // 1
        String sessionId = "testSession";

        BPoint point = new BPoint(x, y, r, sessionId);

        assertTrue(point.isHit());
    }

    @Test
    public void testBPointNotHitInFirstQuarter() {
        // Point in first quarter: x > 0, y > 0 - never hits
        BigFraction x = new BigFraction(1, 2); // 0.5
        BigFraction y = new BigFraction(1, 4); // 0.25
        BigFraction r = new BigFraction(1, 1); // 1
        String sessionId = "testSession";

        BPoint point = new BPoint(x, y, r, sessionId);

        assertFalse(point.isHit());
    }

    @Test
    public void testBPointHitInThirdQuarterCircle() {
        // Point in third quarter: x < 0, y < 0, inside circle x^2 + y^2 <= (r/2)^2
        BigFraction x = new BigFraction(-1, 4); // -0.25
        BigFraction y = new BigFraction(-1, 4); // -0.25
        BigFraction r = new BigFraction(1, 1); // 1
        String sessionId = "testSession";

        BPoint point = new BPoint(x, y, r, sessionId);

        assertTrue(point.isHit());
    }

    @Test
    public void testBPointNotHitInThirdQuarterOutsideCircle() {
        // Point in third quarter: x < 0, y < 0, outside circle x^2 + y^2 > (r/2)^2
        BigFraction x = new BigFraction(-1, 2); // -0.5
        BigFraction y = new BigFraction(-1, 2); // -0.5
        BigFraction r = new BigFraction(1, 1); // 1
        String sessionId = "testSession";

        BPoint point = new BPoint(x, y, r, sessionId);

        assertFalse(point.isHit());
    }
}