/**
 * TableFilter to filter for entries whose two columns match.
 *
 * @author Matthew Owen
 */
public class ColumnMatchFilter extends TableFilter {
    private int index1, index2;

    public ColumnMatchFilter(Table input, String colName1, String colName2) {
        super(input);
        index1 = input.colNameToIndex(colName1);
        index2 = input.colNameToIndex(colName2);
    }

    @Override
    protected boolean keep() {
        return _next.getValue(index1).equals(_next.getValue(index2));
    }

    // FIXME: Add instance variables?
}
