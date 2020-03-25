package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Aayush Sutaria
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycle = cycles.replaceAll("\\s+", "");
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        _cycle = _cycle + cycle;
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        int input = wrap(p);
        char change = _alphabet.toChar(input);
        char perm = permute(change);
        return _alphabet.toInt(perm);

    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        int input = wrap(c);
        char change = _alphabet.toChar(input);
        char inv = invert(change);
        return _alphabet.toInt(inv);
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        if (_cycle.equals("")) {
            return  p;
        }
        if (!_alphabet.contains(p)){
            throw error("not in alphabet");
        }
        int index = _cycle.indexOf((String.valueOf(p)));
        if (index != -1) {
            int next = index + 1;
            if (_cycle.charAt(next) != ')') {
                return _cycle.charAt(next);
            } else {
                while (_cycle.charAt(index) != '(') {
                    index = index - 1;
                }
                return _cycle.charAt(index + 1);
            }
        }
        return p;
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        if (_cycle.equals("") || !_alphabet.contains(c)) {
            return  c;
        }

        int index = _cycle.indexOf((String.valueOf(c)));
        if (index != -1) {
            int back = index - 1;
            if (_cycle.charAt(back) != '(') {
                return _cycle.charAt(back);
            } else {
                while (_cycle.charAt(index) != ')') {
                    index = index + 1;
                }
                return _cycle.charAt(index - 1);
            }
        }
        return c;
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (int i = 0; i < _alphabet.size(); i++) {
            char b = _alphabet.toChar(i);
            if (b == permute(b)) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** An array of strings, each string representing a cycle
     *  that a letter would be converted through. */
    private String _cycle;
}
