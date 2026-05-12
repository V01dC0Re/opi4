import com.github.itmovalesnikov.web3.utils.BigFractionUtils;
import org.apache.commons.math3.fraction.BigFraction;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BigFractionUtilsTest {

    @Test
    public void testFromDecimalStringWithInteger() {
        BigFraction result = BigFractionUtils.fromDecimalString("5");
        assertEquals(new BigFraction(5, 1), result);
    }

    @Test
    public void testFromDecimalStringWithFraction() {
        BigFraction result = BigFractionUtils.fromDecimalString("3/4");
        assertEquals(new BigFraction(3, 4), result);
    }

    @Test
    public void testFromDecimalStringWithDecimal() {
        BigFraction result = BigFractionUtils.fromDecimalString("0.75");
        assertEquals(new BigFraction(3, 4), result);
    }

    @Test
    public void testToDecimalStringWithTerminating() {
        BigFraction fraction = new BigFraction(3, 4); // 0.75 - terminating decimal
        String result = BigFractionUtils.toDecimalString(fraction);
        assertEquals("0.75", result);
    }

    @Test
    public void testToDecimalStringWithNonTerminating() {
        BigFraction fraction = new BigFraction(1, 3); // non-terminating decimal
        String result = BigFractionUtils.toDecimalString(fraction);
        assertEquals("1/3", result);
    }

    @Test
    public void testIsTerminatingReturnsTrue() {
        BigFraction fraction = new BigFraction(3, 4); // 0.75 - terminating
        assertTrue(BigFractionUtils.isTerminating(fraction));
    }

    @Test
    public void testIsTerminatingReturnsFalse() {
        BigFraction fraction = new BigFraction(1, 3); // non-terminating
        assertFalse(BigFractionUtils.isTerminating(fraction));
    }
}