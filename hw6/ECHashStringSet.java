
import java.util.List;
import java.util.LinkedList;

/** A set of String values.
 *  @Aayush Sutaria
 */
class ECHashStringSet implements StringSet {
    private static double minLoad = 0.2;
    private static double maxLoad = 5.0;
    private int size = 0;
    private LinkedList<String>[] _store;

    public ECHashStringSet() {
        _store = new LinkedList[5];
        for (int i = 0; i < 5; i++) {
            _store[i] = new LinkedList<String>();
        }
        size = 0;
    }

    public int getSize() {
        return size;
    }

    @Override
    public void put(String s) {
        if (loadVal() >= maxLoad) {
            resize();
        }
        int i = storeIndex(s);
        _store[i].add(s);
        size++;
    }

    @Override
    public boolean contains(String s) {
        return _store[storeIndex(s)].contains(s);
    }

    @Override
    public List<String> asList() {
        LinkedList<String> list = new LinkedList<String>();
        for(LinkedList<String> temp : _store) {
            list.addAll(temp);
        }
        return list;
    }
    private double loadVal() {
        return (double)size / (double) _store.length;
    }

    private void resize() {
        List store = asList();
        _store = new LinkedList[_store.length * 2];

        for (int i = 0; i < _store.length; i++) {
            _store[i] = new LinkedList<String>();
        }

        for (Object o : store) {
            String str = (String) o;
            int i = storeIndex(str);
            _store[i].add(str);
        }

        }
    private int storeIndex(String str) {
        return (str.hashCode() & 0x7fffffff) % _store.length;
    }
}
