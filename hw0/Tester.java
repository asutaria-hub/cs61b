import org.junit.Test;
import static org.junit.Assert.*;

import ucb.junit.textui;

/** Tests for hw0. 
 *  @author Aayush Sutaria
 */
public class Tester {

    /* Feel free to add your own tests.  For now, you can just follow
     * the pattern you see here.  We'll look into the details of JUnit
     * testing later.
     *
     * To actually run the tests, just use
     *      java Tester 
     * (after first compiling your files).
     *
     * DON'T put your HW0 solutions here!  Put them in a separate
     * class and figure out how to call them from here.  You'll have
     * to modify the calls to max, threeSum, and threeSumDistinct to
     * get them to work, but it's all good practice! */

    @Test
    public void max() {
        // Change call to max to make this call yours.
        assertEquals(14, Main.max(new int[] { 0, -5, 2, 14, 10 }));
        assertEquals(21, Main.max(new int[] { 10, -50, 20, 14, 21 }));
        assertEquals(142, Main.max(new int[] { 1, 0, -200, 142, 121 }));
        assertEquals(1000, Main.max(new int[] { 1000, 230, -20, -142111, 121, 453, 342, 234}));
        // REPLACE THIS WITH MORE TESTS.
    }

    @Test
    public void threeSum() {
        // Change call to threeSum to make this call yours.
        assertTrue(Main.threeSum(new int[] { -6, 3, 10, 200 }));
        assertTrue(Main.threeSum(new int[] { -1, -2, -3, 4, 5, 6 }));
        assertFalse(Main.threeSum(new int[] { -1, -2, -3, -9 }));
        assertFalse(Main.threeSum(new int[] { -6, -2, 5, 13 }));
        // REPLACE THIS WITH MORE TESTS.
    }

    @Test
    public void threeSumDistinctTest() {
        // Change call to threeSumDistinct to make this call yours.
        assertFalse(Main.threeSumDistinct(new int[] { -6, 3, 10, 200 }));
        assertTrue(Main.threeSum(new int[] { -1, -2, -3, 4, 5, 6 }));
        assertTrue(Main.threeSum(new int[] { -12, -1, 13, 40, -5, 6 }));
        assertFalse(Main.threeSum(new int[] { -1, -2, -3, -9 }));
        // REPLACE THIS WITH MORE TESTS.
    }

    public static void main(String[] unused) {
        textui.runClasses(Tester.class);
    }

}
