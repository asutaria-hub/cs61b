package arrays;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *  @author Aayush Sutaria
 */

public class ArraysTest {
    @Test
    public void catenateTest() {
        int[] A = {1, 2, 3};
        int[] B = {4, 5, 6};
        int[] C = {1, 2, 3, 4, 5, 6};
        assertArrayEquals(C, Arrays.catenate(A, B));
    }

    @Test
    public void removeTest() {
        int[] A = {1, 2, 3, 4, 5, 6};
        int[] expected = {1, 5, 6};
        int[] res = Arrays.remove(A, 1, 3);
        assertArrayEquals(expected, res);
    }


    @Test
    public void naturalRunsTest() {
        int[][] expected = {{2, 3, 4}, {1, 5, 8, 9, 11}, {11, 45}};
        int[] testActual = {2, 3, 4, 1, 5, 8, 9, 11, 11, 45};
        assertArrayEquals(expected, Arrays.naturalRuns(testActual));
    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ArraysTest.class));
    }

}

