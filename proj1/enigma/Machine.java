package enigma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Aayush Sutaria
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors;
        listRotors = new ArrayList<>();

        for (Rotor r : allRotors) {
            _allRotorsNames.put(r.name(), r);
        }
        if (numRotors <= 1) {
            throw new EnigmaException("There must be more than 1 rotor slot");
        }
        if (pawls < 0) {
            throw new EnigmaException("There must be 0 or more pawls");
        }
        if (pawls >= numRotors) {
            throw new EnigmaException("There cannot be more pawls"
                    + " than rotors or the same number of pawls as rotors");
        }
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        listRotors.clear();
        for (String r: rotors) {
            if (!_allRotorsNames.containsKey(r)) {
                throw new EnigmaException("Name of rotor does not match");
            } else {
                if (listRotors == null && _allRotorsNames.get(r).reflecting()) {
                    throw new EnigmaException("Fixed rotor is invalid");
                }
                listRotors.add(_allRotorsNames.get(r));
            }
        }
        if (!listRotors.get(0).reflecting()) {
            throw new EnigmaException("Incorrect placement of"
                    + " reflecting rotor");
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != numRotors() - 1) {
            throw new EnigmaException("There is an incorrect number"
                    + " of rotors");
        }
        for (int i = 0; i < setting.length(); i++) {
            listRotors.get(i + 1).set(setting.charAt(i));
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing

     *  the machine. */
    int convert(int c) {
        int res;
        HashMap<Integer, Boolean> machineAdvance =
                new HashMap<>(listRotors.size());
        for (int i = 1; i < listRotors.size(); i++) {
            if (i + 1 < listRotors.size() && listRotors.get(i).rotates()
                    && listRotors.get(i + 1).atNotch()) {
                machineAdvance.put(i, true);
            } else if (listRotors.get(i - 1).rotates()
                    && listRotors.get(i).atNotch()) {
                machineAdvance.put(i, true);
            } else if (i == listRotors.size() - 1) {
                machineAdvance.put(i, true);
            }
        }
        for (int x = 1; x < listRotors.size(); x++) {
            if (machineAdvance.containsKey(x)) {
                if (machineAdvance.get(x)) {
                    listRotors.get(x).advance();
                }
            }
        }
        if (_plugboard != null) {
            res = _plugboard.permute(c);
        } else {
            res = c;
        }
        for (int f = listRotors.size() - 1; f >= 0; f--) {
            Rotor forward = listRotors.get(f);
            res = forward.convertForward(res);
        }
        for (int b = 1; b < listRotors.size(); b++) {
            Rotor backward = listRotors.get(b);
            res = backward.convertBackward(res);
        }
        if (_plugboard != null) {
            res = _plugboard.invert(res);
        }
        return res;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        StringBuilder res = new StringBuilder();
        String edit = msg.replaceAll(" ", "").trim();
        for (int i = 0; i < edit.length(); i++) {
            if (Character.isWhitespace(edit.charAt(i))) {
                res.append(" ");
            } else {
                res.append(_alphabet.toChar
                        (convert(_alphabet.toInt(edit.charAt(i)))));
            }
        }
        return res.toString();
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;
    /** Number of rotors. */
    private int _numRotors;
    /** Number of pawls. */
    private int _pawls;
    /** Collection of all rotors. */
    private Collection<Rotor> _allRotors;
    /** Initial Plugboard. */
    private Permutation _plugboard;
    /** Hashmap to help associate names to rotor.  */
    private HashMap<String, Rotor> _allRotorsNames = new HashMap<>();
    /** My ArrayList of rotors. */
    private ArrayList<Rotor> listRotors;
}
