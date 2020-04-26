import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Note that every sorting algorithm takes in an argument k. The sorting 
 * algorithm should sort the array from index 0 to k. This argument could
 * be useful for some of your sorts.
 *
 * Class containing all the sorting algorithms from 61B to date.
 *
 * You may add any number instance variables and instance methods
 * to your Sorting Algorithm classes.
 *
 * You may also override the empty no-argument constructor, but please
 * only use the no-argument constructor for each of the Sorting
 * Algorithms, as that is what will be used for testing.
 *
 * Feel free to use any resources out there to write each sort,
 * including existing implementations on the web or from DSIJ.
 *
 * All implementations except Counting Sort adopted from Algorithms,
 * a textbook by Kevin Wayne and Bob Sedgewick. Their code does not
 * obey our style conventions.
 */
public class MySortingAlgorithms {

    /**
     * Java's Sorting Algorithm. Java uses Quicksort for ints.
     */
    public static class JavaSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            Arrays.sort(array, 0, k);
        }

        @Override
        public String toString() {
            return "Built-In Sort (uses quicksort for ints)";
        }
    }

    /**
     * Insertion sorts the provided data.
     */
    public static class InsertionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            for (int i = 1; i < k; i++) {
                int val = array[i];
                int j = i - 1;
                while (j >= 0 && array[j] > val) {
                    array[j + 1] = array[j];
                    j--;
                }
                array[j + 1] = val;
            }
        }

        @Override
        public String toString() {
            return "Insertion Sort";
        }
    }

    /**
     * Selection Sort for small K should be more efficient
     * than for larger K. You do not need to use a heap,
     * though if you want an extra challenge, feel free to
     * implement a heap based selection sort (i.e. heapsort).
     */
    public static class SelectionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            for (int i = 0; i < k - 1; i++) {
                int minIndex = i;
                for (int j = i + 1; j < k; j++) {
                    if (array[j] < array[minIndex]) {
                        minIndex = j;
                    }
                }
                int temp = array[minIndex];
                array[minIndex] = array[i];
                array[i] = temp;

            }
        }

        @Override
        public String toString() {
            return "Selection Sort";
        }
    }

    /**
     * Your mergesort implementation. An iterative merge
     * method is easier to write than a recursive merge method.
     * Note: I'm only talking about the merge operation here,
     * not the entire algorithm, which is easier to do recursively.
     */
    public static class MergeSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            mergeSort(array, 0, k - 1);
        }

        @Override
        public String toString() {
            return "Merge Sort";
        }

        void mergeSort(int arr[], int left, int right) {
            if (left < right) {
                int mid = left + (right - left) / 2;

                mergeSort(arr, left, mid);
                mergeSort(arr, mid + 1, right);

                merge(arr, left, mid, right);
            }
        }

        void merge(int arr[], int left, int mid, int right) {
            int i, j, b;
            int len1 = mid - left + 1;
            int len2 = right - mid;

            int[] L = new int[len1];
            int[] radix = new int[len2];

            for (i = 0; i < len1; i++)
                L[i] = arr[left + i];
            for (j = 0; j < len2; j++)
                radix[j] = arr[mid + 1 + j];

            i = 0;
            j = 0;
            b = left;
            while (i < len1 && j < len2) {
                if (L[i] <= radix[j]) {
                    arr[b] = L[i];
                    i++;
                } else {
                    arr[b] = radix[j];
                    j++;
                }
                b++;
            }

            while (i < len1) {
                arr[b] = L[i];
                i++;
                b++;
            }

            while (j < len2) {
                arr[b] = radix[j];
                j++;
                b++;
            }
        }
    }


    /**
     * Your Counting Sort implementation.
     * You should create a count array that is the
     * same size as the value of the max digit in the array.
     */
    public static class CountingSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME: to be implemented
        }

        // may want to add additional methods

        @Override
        public String toString() {
            return "Counting Sort";
        }
    }

    /**
     * Your Heapsort implementation.
     */
    public static class HeapSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME
        }

        @Override
        public String toString() {
            return "Heap Sort";
        }
    }

    /**
     * Your Quicksort implementation.
     */
    public static class QuickSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME
        }

        @Override
        public String toString() {
            return "Quicksort";
        }
    }

    /* For radix sorts, treat the integers as strings of x-bit numbers.  For
     * example, if you take x to be 2, then the least significant digit of
     * 25 (= 11001 in binary) would be 1 (01), the next least would be 2 (10)
     * and the third least would be 1.  The rest would be 0.  You can even take
     * x to be 1 and sort one bit at a time.  It might be interesting to see
     * how the times compare for various values of x. */

    /**
     * LSD Sort implementation.
     */
    public static class LSDSort implements SortingAlgorithm {
        @Override
        public void sort(int[] a, int k) {
            int radix = 1 << 8;
            int w = 4;
            int n = k;
            int[] array = new int[n];

            for (int d = 0; d < w; d++) {
                int[] count = new int[radix + 1];
                for (int i = 0; i < n; i++) {
                    int c = (a[i] >> 8 * d) & radix-1;
                    count[c + 1]++;
                }
                for (int r = 0; r < radix; r++)
                    count[r + 1] += count[r];
                if (d == w - 1) {
                    int shift1 = count[radix] - count[radix / 2];
                    int shift2 = count[radix / 2];
                    for (int r = 0; r < radix / 2; r++)
                        count[r] += shift1;
                    for (int r = radix / 2; r < radix; r++)
                        count[r] -= shift2;
                }
                for (int i = 0; i < n; i++) {
                    int c = (a[i] >> 8 * d) & radix-1;
                    array[count[c]++] = a[i];
                }

                for (int i = 0; i < n; i++) {
                    a[i] = array[i];
                }
            }
        }

        @Override
        public String toString() {
            return "LSD Sort";
        }
    }

    /**
     * MSD Sort implementation.
     */
    public static class MSDSort implements SortingAlgorithm {
        @Override
        public void sort(int[] a, int k) {
            // FIXME
        }

        @Override
        public String toString() {
            return "MSD Sort";
        }
    }

    /** Exchange A[I] and A[J]. */
    private static void swap(int[] a, int i, int j) {
        int swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }


}
