package lists;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 *  @Aayush Sutaria
 */

public class ListsTest {
    @Test
    public void oneListTest() {
        int[][] actual = {{2, 6, 9, 11, 14}};
        IntListList expected = IntListList.list(actual);
        IntList test = IntList.list(2, 6, 9, 11, 14);
        assertEquals(expected, Lists.naturalRuns(test));
    }


    @Test
    public void fullTest() {
        int[][] actual = {{2, 3, 4}, {1, 5, 8, 9, 11}, {11, 45}};
        IntListList expected = IntListList.list(actual);
        int[] testActual = {2, 3, 4, 1, 5, 8, 9, 11, 11, 45};
        IntList test = IntList.list(testActual);
        assertEquals(expected, Lists.naturalRuns(test));
    }

    // It might initially seem daunting to try to set up
    // IntListList expected.
    //
    // There is an easy way to get the IntListList that you want in just
    // few lines of code! Make note of the IntListList.list method that
    // takes as input a 2D array.

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ListsTest.class));
    }
}
