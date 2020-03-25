package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Aayush Sutaria
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        if (notches == null || notches.equals("")) {
            return;
        }
        notch = new int[notches.length()];
        for (int i = 0; i < notches.length(); i++) {
            notch[i] = alphabet().toInt(notches.charAt(i));
        }
    }


    @Override
    boolean rotates() {
        return true;
    }

    @Override
    boolean atNotch() {
        if (notch == null) {
            return false;
        }
        for (int i = 0; i < notch.length; i++) {
            if (setting() == notch[i]) {
                return true;
            }
        }
        return false;
    }

    @Override
    void advance() {
        set(permutation().wrap(setting() + 1));
    }

    /** List of all notches. */
    private int[] notch;
}
