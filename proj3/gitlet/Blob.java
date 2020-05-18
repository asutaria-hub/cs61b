package gitlet;
import java.io.File;
import java.io.IOException;

/** Blob class which uses SHA-1 values to point to specific file content.
 * @author Aayush Sutaria
 */
public class Blob {
    /** Constructor for Blob class which takes in a file name.
     * @param fileName name of input file
     */
    public Blob(String fileName) {
        _fileName = fileName;
        _fileContents = Utils.readContents(Utils.join(Main.CWD, fileName));
        _shaVal = Utils.sha1(_fileContents);
    }


    /** Save file "fname" into file with SHA-1 val of shaVal.
     * @param fName is the file name
     * @param shaVal is a SHA-1 value
     */
    public static void saveContent(String fName, String shaVal)
            throws IOException {
        Utils.join(Main.OBJECT, shaVal).createNewFile();
        Utils.writeContents(Utils.join(Main.CWD, fName),
                Utils.readContentsAsString(Utils.join(Main.OBJECT, shaVal)));

    }

    /** Save file as its SHA-1 val as its name. */
    public static void saveContent() throws IOException {
        Utils.join(Main.OBJECT, _shaVal).createNewFile();
        Utils.writeContents(Utils.join(Main.OBJECT, _shaVal), _fileContents);
    }


    /** get file name.
     * @return file name
     * */
    public String getName() {
        return _fileName;
    }

    /** get file SHA-1.
     * @return file SHA-1
     * */
    public String getShaVal() {
        return _shaVal;
    }

    /** takes in SHA-1 and outputs file content of deserialized blob.
     * @param shaVal is a SHA-1 value
     * @return content of file which the indicated blob points to
     */
    public static String fromFile(String shaVal) {
        File f = Utils.join(Main.OBJECT, shaVal);
        if (!f.exists()) {
            throw new IllegalArgumentException(
                    "No blob file with that name found.");
        }
        return Utils.readContentsAsString(f);
    }

    /** store file name. */
    private static String _fileName;

    /** store file contents. */
    private static byte[] _fileContents;

    /** store file SHA-1. */
    private static String _shaVal;

}
