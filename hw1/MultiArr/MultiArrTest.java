import static org.junit.Assert.*;
import org.junit.Test;

public class MultiArrTest {

    @Test
    public void testMaxValue() {
        int[][] arr = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        int[][] arr2 = {{1, 2, 3}, {4, 5, 6}, {76, 8, 9}};
        assertEquals(9, MultiArr.maxValue(arr));
        assertEquals(76, MultiArr.maxValue(arr2));
    }

    @Test
    public void testAllRowSums() {
        int[][] arr = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        int[][] arr2 = {{1, 2, 3}, {4, 5, 6}, {76, 8, 9}};
        assertArrayEquals(new int[]{6, 15, 24}, MultiArr.allRowSums(arr));
        assertArrayEquals(new int[]{6, 15, 93}, MultiArr.allRowSums(arr2));
    }


    /* Run the unit tests in this file. */
    public static void main(String... args) {
        System.exit(ucb.junit.textui.runClasses(MultiArrTest.class));
    }
}
