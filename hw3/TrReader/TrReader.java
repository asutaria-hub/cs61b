import java.io.Reader;
import java.io.IOException;

/** Translating Reader: a stream that is a translation of an
 *  existing reader.
 *  @author Aayush Sutaria
 */
public class TrReader extends Reader {
    /** A new TrReader that produces the stream of characters produced
     *  by STR, converting all characters that occur in FROM to the
     *  corresponding characters in TO.  That is, change occurrences of
     *  FROM.charAt(i) to TO.charAt(i), for all i, leaving other characters
     *  in STR unchanged.  FROM and TO must have the same length. */
    private Reader str;
    private String from, to;
    public TrReader(Reader str, String from, String to) {
        this.str = str;
        this.from = from;
        this.to = to;

    }

    @Override
    public void close() throws IOException {
        str.close();
    }

    private char replace(char c) {
        int index = from.indexOf(c);
        if (index != -1) {
            c = to.charAt(index);
        }
        return  c;
    }

    @Override
    public int read(char[] chars, int offset, int count) throws IOException {
        int numRead = str.read(chars, offset, count);
        for (int i = offset; i < offset + numRead; i++) {
            chars[i] = replace(chars[i]);
        }
        return numRead;
    }

    /*
     * NOTE: Until you fill in the necessary methods, the compiler will
     *       reject this file, saying that you must declare TrReader
     *       abstract. Don't do that; define the right methods instead!
     */
}
