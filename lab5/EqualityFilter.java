/**
 * TableFilter to filter for entries equal to a given string.
 *
 * @author Matthew Owen
 */
public class EqualityFilter extends TableFilter {
    private String match;
    private int index;

    public EqualityFilter(Table input, String colName, String match) {
        super(input);
        this.match = match;
        index = input.colNameToIndex(colName);


    }

    @Override
    protected boolean keep() {
        String test = _next.getValue(index);
         if (match.equals(test)){
             return true;
         }
         return false;
    }

    // FIXME: Add instance variables?
}
