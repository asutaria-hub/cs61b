package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @Aayush Sutaria
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    private Permutation getNewPermutation(String cycles, Alphabet alphabet) {
        return new Permutation(cycles, alphabet);
    }

    private Alphabet getNewAlphabet(String chars) {
        return new Alphabet(chars);
    }

    private Alphabet getNewAlphabet() {
        return new Alphabet();
    }
    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }


    @Test
    public void testInvertChar() {
        Permutation p = getNewPermutation("(BACD)", getNewAlphabet("ABCD"));
        assertEquals('B', p.invert('A'));
        assertEquals('D', p.invert('B'));

    }

    @Test
    public void testPermChar() {
        Permutation p = getNewPermutation("(BACD)", getNewAlphabet("ABCD"));
        assertEquals('C', p.permute('A'));
        assertEquals('A', p.permute('B'));

    }
    @Test
    public void testInvert() {
        Permutation p = getNewPermutation("(BACD)", getNewAlphabet("ABCD"));
        assertEquals(0, p.invert(6));
        assertEquals(3, p.invert(1));
        assertEquals(2, p.invert(3));

        Permutation p2 = getNewPermutation("(BACD) (EF)",
                getNewAlphabet("ABCDEF"));
        assertEquals(4, p2.invert(5));
        assertEquals(2, p2.invert(3));

        Permutation p3 = getNewPermutation("(B)", getNewAlphabet("ABCDEF"));
        assertEquals(5, p3.invert(5));
    }

    @Test
    public void testPerm() {
        Permutation p = getNewPermutation("(BACD)", getNewAlphabet("ABCD"));
        assertEquals(1, p.permute(7));
        assertEquals(3, p.permute(2));

        Permutation p2 = getNewPermutation("(BACD) (EF)",
                getNewAlphabet("ABCDEF"));
        assertEquals(4, p2.permute(5));
        assertEquals(1, p2.permute(3));

    }

    @Test
    public void testSize() {
        Permutation p = getNewPermutation("(BACD)", getNewAlphabet("ABCD"));
        assertEquals(4, p.size());

    }
}
