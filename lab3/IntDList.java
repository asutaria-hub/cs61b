import net.sf.saxon.lib.SchemaURIResolver;

/**
 * Scheme-like pairs that can be used to form a list of integers.
 *
 * @author P. N. Hilfinger; updated by Vivant Sakore (1/29/2020)
 */
public class  IntDList {

    /**
     * First and last nodes of list.
     */
    protected DNode _front, _back;

    /**
     * An empty list.
     */
    public IntDList() {
        _front = _back = null;
    }

    /**
     * @param values the ints to be placed in the IntDList.
     */
    public IntDList(Integer... values) {
        _front = _back = null;
        for (int val : values) {
            insertBack(val);
        }
    }

    /**
     * @return The first value in this list.
     * Throws a NullPointerException if the list is empty.
     */
    public int getFront() {
        return _front._val;
    }

    /**
     * @return The last value in this list.
     * Throws a NullPointerException if the list is empty.
     */
    public int getBack() {
        return _back._val;
    }

    /**
     * @return The number of elements in this list.
     */
    public int size() {
        if(_front == null || _back == null){
            return  0;
        }
        int tot = 1;
        DNode curr = _front;
        while (curr._next != null) {
            tot = tot + 1;
            curr = curr._next;
        }

        return tot;
    }

    /**
     * @param i index of element to return,
     *          where i = 0 returns the first element,
     *          i = 1 returns the second element,
     *          i = -1 returns the last element,
     *          i = -2 returns the second to last element, and so on.
     *          You can assume i will always be a valid index, i.e 0 <= i < size for positive indices
     *          and -size <= i <= -1 for negative indices.
     * @return The integer value at index i
     */
    public int get(int i) {
        if (_front == null || _back == null) {
            return 0;
        } else if (i >= 0) {
            DNode curr = _front;
            for (int b = 0; b < i; b++) {
                curr = curr._next;
            }
            return curr._val;
        } else {
            DNode curr = _back;
            for (int b = -1; b > i; b--) {
                curr = curr._prev;
            }
            return curr._val;
        }
    }

    /**
     * @param d value to be inserted in the front
     */
    public void insertFront(int d) {
        if (_front == null){
            _front = new DNode(d);
            _back = _front;
        }

        else {
            _front._prev = new DNode(null, d, _front);
            _front = _front._prev;
        }
    }

    /**
     * @param d value to be inserted in the back
     */
    public void insertBack(int d) {
        if (_back == null){
            _back = new DNode(d);
            _front = _back;
        }
        else {
            _back._next = new DNode(_back, d, null);
            _back = _back._next;
        }
    }

    /**
     * @param d     value to be inserted
     * @param index index at which the value should be inserted
     *              where index = 0 inserts at the front,
     *              index = 1 inserts at the second position,
     *              index = -1 inserts at the back,
     *              index = -2 inserts at the second to last position, and so on.
     *              You can assume index will always be a valid index,
     *              i.e 0 <= index <= size for positive indices (including insertions at front and back)
     *              and -(size+1) <= index <= -1 for negative indices (including insertions at front and back).
     */
    public void insertAtIndex(int d, int i) {}
       /*
       if (_front == null || _back == null) {

            _front = _back = new DNode(d);
        }
        else if (i >= 0) {
            curr = _front;
            for (int b = 0; b < i; b++) {
                _front = _front._next;
            }
            _front = new DNode(_front._prev, d, _front);
            _front = curr;
        }
        else {
            curr = _front;
            for (int b = -1; b > i; b--) {
                _back = _back._prev;
            }
            _back = new DNode(_back, d, _back._next);
            _front = curr;
        }
    }

        */


    /**
     * Removes the first item in the IntDList and returns it.
     *
     * @return the item that was deleted
     */
    public int deleteFront() {
        int value = _front._val;
        if (_front == _back){
            _back = null;
            _front = null;
        }
        else {
            _front = _front._next;
            _front._prev = null;
        }
        return value;
    }

    /**
     * Removes the last item in the IntDList and returns it.
     *
     * @return the item that was deleted
     */
    public int deleteBack() {
        int value = _back._val;
        if (_front == _back){
            _back = null;
            _front = null;
        }
        else {
            _back = _back._prev;
            _back._next = null;
        }
        return value;
    }


    /**
     * @param index index of element to be deleted,
     *          where index = 0 returns the first element,
     *          index = 1 will delete the second element,
     *          index = -1 will delete the last element,
     *          index = -2 will delete the second to last element, and so on.
     *          You can assume index will always be a valid index,
     *              i.e 0 <= index < size for positive indices (including deletions at front and back)
     *              and -size <= index <= -1 for negative indices (including deletions at front and back).
     * @return the item that was deleted
     */
    public int deleteAtIndex(int i) {
        if (i == 0){
            int val =_front._val;
            deleteFront();
            return val;

        }

        else if (i == -1){
            int val =_back._val;
            deleteBack();
            return val;
        }

        else if (i > 0) {
            DNode curr = _front;
            for (int b = 0; b < i; b++) {
                curr = curr._next;
            }
           int tot = curr._val;
            curr._prev._next = curr._next;

            if (curr._next != null) {
                curr._next._prev = curr._prev;
            }
            else {
                _back = curr._prev;
            }
            return tot;
        }

        else {
            DNode curr = _back;
            for (int b = -1; b > i; b--) {
                curr = curr._prev;
            }
            int tot = curr._val;
            curr._next._prev = curr._prev;

            if (curr._prev != null) {
                curr._prev._next = curr._next;

            }

            else {
                _front = curr._next;
            }

            return tot;
        }
    }

    /**
     * @return a string representation of the IntDList in the form
     * [] (empty list) or [1, 2], etc.
     * Hint:
     * String a = "a";
     * a += "b";
     * System.out.println(a); //prints ab
     */
    public String toString() {
        if (size() == 0) {
            return "[]";
        }
        String str = "[";
        DNode curr = _front;
        for (; curr._next != null; curr = curr._next) {
            str += curr._val + ", ";
        }
        str += curr._val +"]";
        return str;
    }

    /**
     * DNode is a "static nested class", because we're only using it inside
     * IntDList, so there's no need to put it outside (and "pollute the
     * namespace" with it. This is also referred to as encapsulation.
     * Look it up for more information!
     */
    static class DNode {
        /** Previous DNode. */
        protected DNode _prev;
        /** Next DNode. */
        protected DNode _next;
        /** Value contained in DNode. */
        protected int _val;

        /**
         * @param val the int to be placed in DNode.
         */
        protected DNode(int val) {
            this(null, val, null);
        }

        /**
         * @param prev previous DNode.
         * @param val  value to be stored in DNode.
         * @param next next DNode.
         */
        protected DNode(DNode prev, int val, DNode next) {
            _prev = prev;
            _val = val;
            _next = next;
        }
    }

}
