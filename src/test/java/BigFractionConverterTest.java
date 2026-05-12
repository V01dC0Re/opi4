import com.github.itmovalesnikov.web3.utils.BigFractionConverter;
import org.apache.commons.math3.fraction.BigFraction;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BigFractionConverterTest {

    @Test
    public void testConvertToDatabaseColumn() {
        BigFractionConverter converter = new BigFractionConverter();
        BigFraction fraction = new BigFraction(3, 4);

        String result = converter.convertToDatabaseColumn(fraction);

        assertEquals("0.75", result);
    }

    @Test
    public void testConvertToEntityAttribute() {
        BigFractionConverter converter = new BigFractionConverter();
        String dbData = "0.75";

        BigFraction result = converter.convertToEntityAttribute(dbData);

        assertEquals(new BigFraction(3, 4), result);
    }

    @Test
    public void testRoundTripConversion() {
        BigFractionConverter converter = new BigFractionConverter();
        BigFraction original = new BigFraction(2, 3);

        // Convert to DB string
        String dbString = converter.convertToDatabaseColumn(original);

        // Convert back to BigFraction
        BigFraction convertedBack = converter.convertToEntityAttribute(dbString);

        assertEquals(original, convertedBack);
    }

    @Test
    public void testFractionRoundTrip() {
        BigFractionConverter converter = new BigFractionConverter();
        BigFraction original = new BigFraction(1, 3);

        // Convert to DB string
        String dbString = converter.convertToDatabaseColumn(original);

        // Convert back to BigFraction
        BigFraction convertedBack = converter.convertToEntityAttribute(dbString);

        assertEquals(original, convertedBack);
    }
}