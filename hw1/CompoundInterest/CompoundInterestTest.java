import static org.junit.Assert.*;
import org.junit.Test;

public class CompoundInterestTest {

    @Test
    public void testNumYears() {
        assertEquals(2, CompoundInterest.numYears(2022));
        assertEquals(-1, CompoundInterest.numYears(2019));
        assertEquals(1, CompoundInterest.numYears(2021));
        assertEquals(0, CompoundInterest.numYears(2020));
        assertEquals(19, CompoundInterest.numYears(2039));
    }

    @Test
    public void testFutureValue() {
        double tolerance = 0.01;
        assertEquals(10.0, CompoundInterest.futureValue(10.0, 200.0, 2020), tolerance);
        assertEquals(-100.0, CompoundInterest.futureValue(100.0, -200.0, 2021), tolerance);
        assertEquals(1.0, CompoundInterest.futureValue(1.0, 50, 2020), tolerance);
        assertEquals(1.0, CompoundInterest.futureValue(1.0, -50, 2020), tolerance);
    }

    @Test
    public void testFutureValueReal() {
        double tolerance = 0.01;
        assertEquals(10.864, CompoundInterest.futureValueReal(10.0, 12.0, 2021, 3.0),
                tolerance);
        assertEquals(304858.0282465284, CompoundInterest.futureValueReal(1000000, 0, 2059, 3.0),
                tolerance);
    }


    @Test
    public void testTotalSavings() {
        double tolerance = 0.01;
        assertEquals(10500, CompoundInterest.totalSavings(5000, 2021, 10), tolerance);
        assertEquals(5000.0, CompoundInterest.totalSavings(5000, 2020, -10), tolerance);
    }

    @Test
    public void testTotalSavingsReal() {
        double tolerance = 0.01;
        assertEquals(9450.0, CompoundInterest.totalSavingsReal(5000, 2021, 10, 10), tolerance);
        assertEquals(5000.0, CompoundInterest.totalSavingsReal(5000, 2020, -10, 10), tolerance);
    }


    /* Run the unit tests in this file. */
    public static void main(String... args) {
        System.exit(ucb.junit.textui.runClasses(CompoundInterestTest.class));
    }
}
