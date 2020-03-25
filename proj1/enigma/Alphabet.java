package enigma;


/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Aayush Sutaria
 */

class Alphabet {
    /** characters of the alphabet. */
    private String _chars;

    /** A new alphabet containing CHARS.  Character number #k has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        _chars = chars;
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _chars.length();
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        char[] charArray;
        charArray = new char[_chars.length()];
        charArray = _chars.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if (ch == charArray[i]) {
                return true;
            }
        }
        return false;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        char[] charArray;
        charArray = new char [_chars.length()];
        charArray = _chars.toCharArray();
        return charArray[index];
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        char[] charArray;
        charArray = new char [_chars.length()];
        charArray = _chars.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if (ch == charArray[i]) {
                return i;
            }
        }
        return -1;
    }

}
