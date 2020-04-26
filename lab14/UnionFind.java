import java.lang.reflect.Array;

/** Disjoint sets of contiguous integers that allows (a) finding whether
 *  two integers are in the same set and (b) unioning two sets together.  
 *  At any given time, for a structure partitioning the integers 1 to N, 
 *  into sets, each set is represented by a unique member of that
 *  set, called its representative.
 *  @author Aayush Sutaria
 */
public class UnionFind {

    /** A union-find structure consisting of the sets { 1 }, { 2 }, ... { N }.
     */
    public UnionFind(int N) {
        arr = new int[N + 1];
        size = new int[N + 1];
        for (int i =1; i < arr.length; i++) {
            arr[i] = i;
            size[i] = 1;
        }
    }

    /** Return the representative of the set currently containing V.
     *  Assumes V is contained in one of the sets.  */
    public int find(int v) {
        while (v != arr[v]) {
            v = arr[arr[v]];
        }
        return v;
    }

    /** Return true iff U and V are in the same set. */
    public boolean samePartition(int u, int v) {
        return find(u) == find(v);
    }

    /** Union U and V into a single set, returning its representative. */
    public int union(int u, int v) {
        u = find(u);
        v = find(v);
        if (u == v) {
            return find(u);
        }
        if (size[u] > size[v]) {
            arr[v] = u;
            size[u] = size[u] + size[v];
            return u;
        } else {
            arr[u] = v;
            size[v] = size[v] + size[u];
            return v;
        }
    }

    private int[] arr;
    private int[] size;
}
