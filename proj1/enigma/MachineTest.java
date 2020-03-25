package enigma;

import java.util.ArrayList;
import java.util.Collection;
import org.junit.Test;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Machine class.
 *  @author Aayush Sutaria
 */



public class MachineTest {
    private ArrayList<Rotor> r = ROTORLIST;
    private Machine machine;
    private String[] order = {"B", "BETA", "III", "IV", "I"};

    private void setMachine(Alphabet alpha, int numRotors,
                            int pawls, Collection<Rotor> allRotors) {
        machine = new Machine(alpha, numRotors, pawls, allRotors);
    }


    @Test
    public void numRotors() {
        setMachine(UPPER, 5, 3, r);
        assertEquals(5, machine.numRotors());
    }

    @Test
    public void numPawls() {
        setMachine(UPPER, 5, 3, r);
        assertEquals(3, machine.numPawls());
    }


    @Test
    public void testConvert() {
        setMachine(UPPER, 5, 3, r);
        machine.insertRotors(order);
        machine.setRotors("AXLE");
        machine.setPlugboard(new Permutation("(HQ) (EX) (IP) (TR) (BY)",
                UPPER));
        assertEquals("QVPQ", machine.convert("FROM"));
        setMachine(UPPER, 5, 3, r);
        machine.insertRotors(order);
        machine.setRotors("AXLE");
        machine.setPlugboard(new Permutation("(HQ) (EX) (IP) (TR) (BY)",
                UPPER));
        assertEquals("FROM", machine.convert("QVPQ"));
    }
}
