import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * Implementation of a BST based String Set.
 * @Aayush Sutaria
 */
public class BSTStringSet implements SortedStringSet, Iterable<String> {
    /** Creates a new empty set. */
    public BSTStringSet() {
        _root = null;
    }

    @Override
    public void put(String s) {
        _root = putHelper(_root, s);
    }
    private Node putHelper(Node node, String string) {
        if (node == null) {
            node = new  Node(string);
        } else {
            if(node.s.compareTo(string) > 0) {
                node.left = putHelper(node.left, string);
            } else if (node.s.compareTo(string) < 0) {
                node.right = putHelper(node.right, string);
            } else {
                node.s = string;
            }
        }
        return node;
    }

    @Override
    public boolean contains(String s) {
        if (_root == null) {
            return false;
        } else {
            Node next = _root;
            while (next != null) {
                if (next.s.equals(s)) {
                    return true;
                }
                if (next.s.compareTo(s) > 0) {
                    next = next.left;
                } else if (next.s.compareTo(s) < 0) {
                    next = next.right;
                }
            }
        }
        return false;
        }


    @Override
    public List<String> asList() {
        ArrayList<String> res = new ArrayList<String>();
        for (String val: this) {
            res.add(val);
        }
        return res;
    }


    /** Represents a single Node of the tree. */
    private static class Node {
        /** String stored in this Node. */
        private String s;
        /** Left child of this Node. */
        private Node left;
        /** Right child of this Node. */
        private Node right;

        /** Creates a Node containing SP. */
        Node(String sp) {
            s = sp;
        }
    }

    /** An iterator over BSTs. */
    private static class BSTIterator implements Iterator<String> {
        /** Stack of nodes to be delivered.  The values to be delivered
         *  are (a) the label of the top of the stack, then (b)
         *  the labels of the right child of the top of the stack inorder,
         *  then (c) the nodes in the rest of the stack (i.e., the result
         *  of recursively applying this rule to the result of popping
         *  the stack. */
        private Stack<Node> _toDo = new Stack<>();

        /** A new iterator over the labels in NODE. */
        BSTIterator(Node node) {
            addTree(node);
        }

        @Override
        public boolean hasNext() {
            return !_toDo.empty();
        }

        @Override
        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Node node = _toDo.pop();
            addTree(node.right);
            return node.s;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        /** Add the relevant subtrees of the tree rooted at NODE. */
        private void addTree(Node node) {
            while (node != null) {
                _toDo.push(node);
                node = node.left;
            }
        }
    }

    @Override
    public Iterator<String> iterator() {
        return new BSTIterator(_root);
    }

    @Override
    public Iterator<String> iterator(String low, String high) {
        return new BSTOrderIterator(_root, low, high);
    }
    private static class BSTOrderIterator implements Iterator<String> {
        /** Stack of nodes to be delivered.  The values to be delivered
         *  are (a) the label of the top of the stack, then (b)
         *  the labels of the right child of the top of the stack inorder,
         *  then (c) the nodes in the rest of the stack (i.e., the result
         *  of recursively applying this rule to the result of popping
         *  the stack. */
        private Stack<Node> _toDo = new Stack<>();
        private String _low;
        private String _high;

        /** A new iterator over the labels in NODE. */
        BSTOrderIterator(Node node, String low, String high) {
            _low = low;
            _high = high;
            addTree(node);
        }

        @Override
        public boolean hasNext() {
            return !_toDo.empty() && _toDo.peek().s.compareTo(_high) <= 0;
        }

        @Override
        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Node node = _toDo.pop();
            addTree(node.right);
            return node.s;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        /** Add the relevant subtrees of the tree rooted at NODE. */
        private void addTree(Node node) {
            while (node != null && node.s.compareTo(_low) >= 0) {
                _toDo.push(node);
                node = node.left;
            }
            if (node != null) {
                addTree(node.right);
            }
        }
    }

    /** Root node of the tree. */
    private Node _root;
}
