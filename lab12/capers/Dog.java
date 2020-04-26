package capers;

import java.io.*;

/** Represents a dog that can be serialized.
 * @author Sean Dooher
*/
public class Dog {

    /** Folder that dogs live in. */
    static final File DOG_FOLDER = new File(".capers/dogs");

    /**
     * Creates a dog object with the specified parameters.
     * @param name Name of dog
     * @param breed Breed of dog
     * @param age Age of dog
     */
    public Dog(String name, String breed, int age) {
        _age = age;
        _breed = breed;
        _name = name;
    }

    /**
     * Reads in and deserializes a dog from a file with name NAME in DOG_FOLDER.
     *
     * @param name Name of dog to load
     * @return Dog read from file
     */
    public static Dog fromFile(String name) {
        try {
            BufferedReader in = new BufferedReader(
                    new FileReader(name));
            Dog dog = new Dog(name, in.readLine(), Integer.parseInt(in.readLine()));
            return dog;
        }
        catch (IOException e) {
            System.out.println("Exception Occurred" + e);
        }
        return null;
    }

    /**
     * Increases a dog's age and celebrates!
     */
    public void haveBirthday() {
        _age += 1;
        System.out.println(toString());
        System.out.println("Happy birthday! Woof! Woof!");
    }

    /**
     * Saves a dog to a file for future use.
     */
    public void saveDog() {
        File dog = new File(".capers/dogs/" + this._name);
        try {
            dog.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        appendStrToFile(this._name, this._breed, true);
        appendStrToFile(this._name, Integer.toString(this._age), true);

    }

    public void saveDoggie() {
        File dog = new File(".capers/dogs/" + this._name);
        try {
            dog.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedWriter out = new BufferedWriter(
                    new FileWriter(this._name));
            out.write(this._breed+"\n");
            out.write(Integer.toString(this._age));
            out.close();
        } catch (IOException e) {
            System.out.println("Exception Occurred" + e);
        }
    }

    @Override
    public String toString() {
        return String.format(
            "Woof! My name is %s and I am a %s! I am %d years old! Woof!",
            _name, _breed, _age);
    }

    /** Age of dog. */
    private int _age;
    /** Breed of dog. */
    private String _breed;
    /** Name of dog. */
    private String _name;
    public static void appendStrToFile(String fileName,
                                       String str, boolean append)
    {
        try {

            // Open given file in append mode.
            BufferedWriter out = new BufferedWriter(
                    new FileWriter(fileName, append));
            out.write(str + "\n");
            out.close();
        }
        catch (IOException e) {
            System.out.println("exception occoured" + e);
        }
    }
}
