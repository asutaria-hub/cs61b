package gitlet;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

/** Class History which is the history of commits.
 * @author Aayush Sutaria
 */

public class History implements Serializable {
    /** History constructor. */
    public History() {
        _commitHistory = new HashMap<>();
    }
    /** add commit c to _commitHistory.
     * @param c commit */
    public void addCommit(Commit c) throws IOException {
        c.getTree().saveTree();
        c.saveCommit();
        _commitHistory.put(c.getCommitShaVal(), c);
    }
    /** Return if _commitHistory is empty. */
    public boolean isEmpty() {
        return _commitHistory.isEmpty();
    }
    /** Return commit history. */
    public HashMap<String, Commit> getCommitHistory() {
        return _commitHistory;
    }

    /** Return commit with shaVal from history.
     * @param shaVal of commit */
    public Commit getCommit(String shaVal) {
        return _commitHistory.get(shaVal);
    }

    /** Takes in SHA-1 and outputs file content of deserialized history.
     * @param b is a branch
     * @return history from file
     */
    public static History fromFile(String b) {
        if (!b.contains("-")) {
            if (!Utils.join(Main.HISTORY, "current").exists()) {
                throw new IllegalArgumentException(
                        "No history file found.");
            }
            return Utils.readObject(Utils.join(Main.HISTORY, "current"),
                    History.class);
        } else {
            return remFromFile(b.substring(0, b.indexOf("-")));
        }
    }

    /** Save History to a file. */
    public void saveHistory() throws IOException {
        Utils.join(Main.HISTORY, "current").createNewFile();
        Utils.writeObject(Utils.join(Main.HISTORY, "current"), this);
    }

    /** Save REMOTE r History to a file.
     * @param r remote */
    public void saveHistory(String r) throws IOException {
        Utils.join(Main.HISTORY, r).createNewFile();
        Utils.writeObject(Utils.join(Main.HISTORY, r), this);
    }

    /** Takes in SHA-1 and outputs file content of deserialized commit.
     * @param r is remote
     * @return history from file
     */
    public static History remFromFile(String r) {
        if (!Main.HISTORY.exists()) {
            throw new IllegalArgumentException(
                    "No history file found.");
        }
        return Utils.readObject(Utils.join(Main.HISTORY, r), History.class);
    }

    /** Hashmap of the history of commits with key:SHA-1 commit Id of commit
 *  and value:commit. */
    private HashMap<String, Commit> _commitHistory;
}
