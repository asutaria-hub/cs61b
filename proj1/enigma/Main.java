package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Aayush Sutaria
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        if (!_input.hasNext()) {
            throw error("There is no input available");
        }
        Machine machine = readConfig();
        _config.close();
        int lines = 0;
        while (_input.hasNextLine()) {
            String cur = _input.nextLine();
            if (cur.isEmpty()) {
                printMessageLine(cur);
            } else {
                if (cur.trim().charAt(0) == '*') {
                    lines = 1 + lines;
                    setUp(machine, cur);
                } else {
                    if (lines == 0) {
                        throw error("The first line must "
                                + "have * at the front/start");
                    } else {
                        printMessageLine(machine.convert(cur));
                    }
                }
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            _alphabet = new Alphabet(_config.nextLine());
            if (!_config.hasNext()) {
                throw error("Configuration file does not have data");
            }
            for (int i = 0; i < 2; i++) {
                if (i == 0) {
                    if (_config.hasNextInt()) {
                        numRotors = _config.nextInt();
                    } else {
                        throw error("Missing data for the"
                                + " number of rotors");
                    }
                } else {
                    if (_config.hasNextInt()) {
                        numPawls = _config.nextInt();
                    } else {
                        throw error("Missing data for the"
                                + " number of pawls");
                    }
                }
            }
            if (_config.hasNext()) {
                rSetting = _config.next();
            } else {
                throw error("Missing data for the"
                        + "rotor setting specs");
            }
            while (_config.hasNext()) {
                rName = rSetting;
                if (!rName.contains("(") || !rName.contains(")")) {
                    Rotor newRotor = readRotor();
                    allRotors.add(newRotor);
                } else {
                    throw error("Rotor name cannot be a cycle");
                }
            }
            return new Machine(_alphabet, numRotors, numPawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String nextLine;
            if (_config.hasNext()) {
                nextLine = _config.next();
            } else {
                throw error("Rotor description is wrong");
            }
            StringBuilder cycles = new StringBuilder();
            rSetting = _config.next();
            for (; rSetting.startsWith("("); ) {
                cycles.append(rSetting);
                if (!_config.hasNext()) {
                    break;
                } else {
                    rSetting = _config.next();
                }
            }
            Permutation perm = new Permutation(cycles.toString(), _alphabet);
            String checkerRotors = Character.toString(nextLine.charAt(0));
            if (checkerRotors.contentEquals("R")) {
                if (nextLine.length() <= 1) {
                    Reflector reflector = new Reflector(rName, perm);
                    return reflector;
                } else {
                    throw error("There should "
                            + "be no notch on a reflector");
                }
            } else if (checkerRotors.contentEquals("N")) {
                if (nextLine.length() <= 1) {
                    FixedRotor fixedRotor = new FixedRotor(rName, perm);
                    return fixedRotor;
                } else {
                    throw error("There should "
                            + "be no notch on a fixed rotor");
                }
            } else if (checkerRotors.contentEquals("M")) {
                if (nextLine.length() >= 2) {
                    MovingRotor movingRotor = new
                            MovingRotor(rName, perm, nextLine.substring(1));
                    return movingRotor;
                } else {
                    throw error("There should "
                            + "be a notch on a moving rotor");
                }
            } else {
                throw error("There is no "
                        + "description for this rotor");
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        Scanner scanner = new Scanner(settings);
        StringBuilder pSettings = new StringBuilder("");
        ArrayList<String> cArray = new ArrayList<>();
        String[] insertRotors = new String[M.numRotors()];
        scanner.next("\\*");
        int i = 0;
        for (; scanner.hasNext(); i++) {
            cArray.add(i, scanner.next());
        }
        if (numRotors >= i) {
            throw error("Incorrect setting of rotors");
        }
        for (int b = 0; b < numRotors; b++) {
            insertRotors[b] = cArray.get(b);
        }
        if (numRotors > 0) {
            cArray.subList(0, numRotors).clear();
        }
        String stringToSet = cArray.get(0);
        cArray.remove(0);
        for (String s : cArray) {
            pSettings.append(s);
        }
        if (numRotors - 1 != stringToSet.length()) {
            throw error("Setting is incorrect");
        }
        M.insertRotors(insertRotors); M.setRotors(stringToSet);
        HashMap<Character, Integer> checker = new HashMap<>();
        for (int x = 0; x < pSettings.length(); x++) {
            if (pSettings.charAt(x) == '(') {
                if (!checker.containsKey(pSettings.charAt(x))) {
                    checker.put(pSettings.charAt(x), 1);
                } else {
                    checker.put(pSettings.charAt(x),
                            checker.get('(') + 1);
                }
            }
            if (pSettings.charAt(x) == ')') {
                if (!checker.containsKey(pSettings.charAt(x))) {
                    checker.put(pSettings.charAt(x), 1);
                } else {
                    checker.put(pSettings.charAt(x),
                            checker.get(')') + 1);
                }
            }
        }
        if (!checker.isEmpty()) {
            if (!checker.get('(').equals(checker.get(')'))) {
                throw error("Incorrect permutation format");
            }
            if (pSettings.length() != 0
                    && checker.get('(').equals(0)) {
                throw error("Incorrect permutation format");
            }
        }
        Permutation pPerm =
                new Permutation(pSettings.toString(), _alphabet);
        M.setPlugboard(pPerm);
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        int group = 5;
        for (int i = 0; i < msg.length(); i++) {
            if (group == 0) {
                _output.print(" ");
                group = 5;
            }
            if (msg.charAt(i) != ' ') {
                _output.print(msg.charAt(i));
                group = group - 1;
            }
        }
        _output.println();
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;
    /** number of rotors. */
    private int numRotors;
    /** number of pawls. */
    private int numPawls;
    /** Specs for rotor settings. */
    private String rSetting;
    /** Rotor name. */
    private String rName;
    /** ArrayList of all Rotors. */
    private ArrayList<Rotor> allRotors = new ArrayList<>();
}
