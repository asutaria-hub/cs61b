/**
 * TableFilter to filter for containing substrings.
 *
 * @author Matthew Owen
 */
public class SubstringFilter extends TableFilter {
    private String subStr;
    private int index;

    public SubstringFilter(Table input, String colName, String subStr) {
        super(input);
        this.subStr = subStr;
        index = input.colNameToIndex(colName);
    }

    @Override
    protected boolean keep() {
        return _next.getValue(index).contains(subStr);
    }

    // FIXME: Add instance variables?
}
