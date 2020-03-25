package image;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *  @author Aayush Sutaria
 */

public class MatrixUtilsTest {
    @Test
    public void fullTest() {
        double[][] image = {{1000000, 1000000, 1000000, 1000000},
                            {1000000, 75990, 30003, 1000000},
                            {1000000, 30002, 103046, 1000000},
                            {1000000, 29515, 38273, 1000000},
                            {1000000, 73403, 35399, 1000000},
                            {1000000, 100000, 100000, 1000000}};
        double[][] outputImage =  {{1000000, 1000000, 1000000, 1000000},
                                   {2000000, 1075990, 1030003, 2000000},
                                   {2075990, 1060005, 1133049, 2030003},
                                   {2060005, 1089520, 1098278, 2133049},
                                   {2089520, 1162923, 1124919, 2098278},
                                   {2162923, 2124919, 2124919, 2124919}};
        double[][] a = MatrixUtils.accumulateVertical(image);
        assert (outputImage.equals(a));
        int[] expected = {1, 2, 1, 1, 2, 1};
        assert (expected.equals(MatrixUtils.findSeam(outputImage,
                MatrixUtils.Orientation.VERTICAL)));

        double[][] test = {{10, 4, 5, 6}, {3, 10, 18, 6}, {8, 5, 19, 6}};
        double[][] output = {{10, 7, 12, 18}, {3, 13, 25, 18}, {8, 8, 27, 31}};
        int[] route = {1, 0, 0, 0};

    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(MatrixUtilsTest.class));
    }
}
