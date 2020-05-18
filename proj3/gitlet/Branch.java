package gitlet;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;

/** Class Branch.
 * @author Aayush Sutaria
 */
public class Branch implements Serializable {
    /** Branch constructor.
     * @param name is the name the branch will be given
     */
    public Branch(String name) {
        _branchName = name;
    }

    /** Branch constructor.
     * @param name is the name the branch will be given
     * @param cHead is the SHA-1 val of the head commit of branch
     */
    public Branch(String name, String cHead) {
        _branchName = name;
        _branchHead = cHead;
    }
    /** Copy from other branch.
     * @param b is a branch **/
    public void copyBranch(Branch b) {
        _branchHead = b.getHead();
        _branchCommits = b.getCommits();
    }

    /** Change branch name to newName.
     * @param newName is the new name of branch */
    public void changeName(String newName) {
        _branchName = newName;
    }

    /** Change branch head to newHead.
     * @param newHead new Head */
    public void changeHead(String newHead) {
        _branchHead = newHead;
        Utils.writeContents(Utils.join(Main.BRANCH, _branchName), newHead);
    }
    /** Return Name of branch. */
    public String getName() {
        return _branchName;
    }

    /** Return Head of branch. */
    public String getHead() {
        return _branchHead;
    }
    /** Return LinkedList of the SHA1 vals for commits in this branch. */
    public LinkedList<String> getCommits() {
        return _branchCommits;
    }

    /** Return LinkedList size. */
    public int getCommitSize() {
        return _branchCommits.size();
    }

    /** Return if Commit is in _branchCommits.
     * @param shaVal SHA-1 Val of the commit */
    public boolean hasCommit(String shaVal) {
        return _branchCommits.contains(shaVal);
    }
    /** Return SHA1-Val of commit at index idx.
     * @param idx index
     * */
    public String getCommit(int idx) {
        return _branchCommits.get(idx);
    }

    /** Add commit to this branch.
     *@param shaVal sha val of commit needed to be added*/
    public void addCommit(String shaVal) {
        _branchCommits.add(shaVal);
        changeHead(shaVal);
    }

    /** Add commits to this branch.
     * @param commits commits */
    public void addCommits(LinkedList<String> commits) throws IOException {
        _branchCommits.addAll(commits);
        saveBranch();
    }

    /** Save branch to a file. */
    public void saveBranch() throws IOException {
        Utils.join(Main.BRANCH, _branchName).createNewFile();
        Utils.writeObject(Utils.join(Main.BRANCH, _branchName), this);
    }

    /** Takes in and deserializes curr branch.
     * @return curr branch
     */
    public static Branch fromFile() {
        if (!Main.CURRENT_BRANCH.exists()) {
            throw new IllegalArgumentException(
                    "No current branch file found.");
        }
        String cur = Utils.readContentsAsString(Main.CURRENT_BRANCH);
        return fromFile(cur);
    }

    /** Takes in and deserializes branch name and return corresponding branch.
     * @return branch with name
     * @param name name of branch
     */
    public static Branch fromFile(String name) {
        if (!Utils.join(Main.BRANCH, name).exists()) {
            throw new IllegalArgumentException(
                    "No branch file with that name found.");
        }
        return Utils.readObject(Utils.join(Main.
                BRANCH, name), Branch.class);
    }

    /** Name of branch. */
    private String _branchName;
    /** SHA-1 val of the head commit of branch. */
    private String _branchHead = "";
    /** LinkedList of the SHA1 vals for commits in this branch. */
    private LinkedList<String> _branchCommits = new LinkedList<>();


}
