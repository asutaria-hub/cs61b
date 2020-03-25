package arrays;

/* NOTE: The file Arrays/Utils.java contains some functions that may be useful
 * in testing your answers. */

/** HW #2 */



/** Array utilities.
 *  @author
 */
class Arrays {

    /* C1. */
    /** Returns a new array consisting of the elements of A followed by the
     *  the elements of B. */
    static int[] catenate(int[] A, int[] B) {
        int countA = 0;
        int [] C = new int[A.length + B.length];

        for (int i = 0; i < A.length; i++) {
            C[i] = A[i];
            countA = countA + 1;
        }
        for (int i = 0; i < B.length; i++) {
            C[i + countA] = B[i];
        }

        return C;
    }

    /* C2. */
    /** Returns the array formed by removing LEN items from A,
     *  beginning with item #START. */
    static int[] remove(int[] A, int start, int len) {
        int [] res = new int [A.length - (len)];
        int count = 0;
        int count2 = 0;
        for (int i = 0; i < start; i++) {
            res[i] = A[i];
            count++;
        }
        for (int i = start + len; i < A.length; i++) {
            res[count + count2] = A[i];
            count2++;
        }
        return res;
    }

    /* C3. */
    /** Returns the array of arrays formed by breaking up A into
     *  maximal ascending lists, without reordering.
     *  For example, if A is {1, 3, 7, 5, 4, 6, 9, 10}, then
     *  returns the three-element array
     *  {{1, 3, 7}, {5}, {4, 6, 9, 10}}. */
    static int[][] naturalRuns(int[] A) {
        if (A == null) {
            return null;
        }

        if (A.length == 0) {
            int [] res = {};
        }

        int count = 1;
        for (int i = 0; i < A.length - 1; i++) {
            if (A[i + 1] <= A[i]) {
                count = count + 1;
            }
        }
        int [][] res = new int [count][];
        int b = 1; int index = 0;
        for (int i = 0; i < count; i++) {
            for (int j = index; j < A.length - 1; j++) {
                if (A[j] >= A[j + 1]) {
                    res[i] = new int[b];
                    for (int k = 0; k < b; k++) {
                        res[i][k] = A[index + k];
                    }
                    index = j + 1;
                    b = 1;
                    break;
                }
                b += 1;
            }
        }

        res[count - 1] = new int[b];
        for (int k = 0; k < b; k++) {
            res[count - 1][k] = A[index + k];
        }



        return res;
    }
}
