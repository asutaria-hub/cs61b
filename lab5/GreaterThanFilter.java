/**
 * TableFilter to filter for entries greater than a given string.
 *
 * @author Matthew Owen
 */
public class GreaterThanFilter extends TableFilter {
    private int length, index;

    public GreaterThanFilter(Table input, String colName, String ref) {
        super(input);
        length = ref.length();
        index = input.colNameToIndex(colName);


    }

    @Override
    protected boolean keep() {
        return _next.getValue(index).length() > length;
    }

    // FIXME: Add instance variables?
}
