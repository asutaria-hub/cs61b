
/** Multidimensional array
 *  @author Aayush Sutaria
 */

public class MultiArr {

    /**
     * {{“hello”,"you",”world”} ,{“how”,”are”,”you”}} prints:
     * Rows: 2
     * Columns: 3
     * <p>
     * {{1,3,4},{1},{5,6,7,8},{7,9}} prints:
     * Rows: 4
     * Columns: 4
     */
    public static void printRowAndCol(int[][] arr) {
        int row = arr.length;
        int col = arr[0].length;
        System.out.println("Rows: " + row);
        System.out.println("Columns: " + col);
    }

    /**
     * @param arr: 2d array
     * @return maximal value present anywhere in the 2d array
     */
    public static int maxValue(int[][] arr) {
        int max = arr[0][0];
        for (int i = 0; i < arr.length; i++) {
            for (int b = 0; b < arr[i].length; b++) {
                if (arr[i][b] > max) {
                    max = arr[i][b];
                }
            }
        }
        return max;
    }

    /**
     * Return an array where each element is the sum of the
     * corresponding row of the 2d array
     */
    public static int[] allRowSums(int[][] arr) {
        int[] arr1 = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            int sum = 0;
            for (int b = 0; b < arr[i].length; b++) {
                sum = sum + arr[i][b];
            }
            arr1[i] = sum;
        }
        return arr1;
    }
}
