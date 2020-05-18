package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

/** The remote class for remote commands.
 * @author Aayush Sutaria
 */

public class Remote implements Serializable {
    /** The current working directory for remote. */
    private File cwd;
    /** GitLet repo directory for remote. */
    private File repo;
    /** GitLet object directory for remote. */
    private File object;
    /** GitLet branch information. */
    private File branch;
    /** GitLet staging area directory. */
    private File stage;
    /** GitLet commit history file. */
    private File history;

    /** Constructor of remote with NAME and DIRECTORY. */
    public Remote(String name, String directory) throws IOException {
        _name = name;
        repo = new File(directory);
        cwd = Utils.join(repo, "..");
        object = Utils.join(repo, "object");
        branch = Utils.join(repo, "branch");
        stage = Utils.join(repo, "stage");
        history = Utils.join(repo, "history");
        saveRemote();
    }

    /** takes in SHA-1 and outputs file content of deserialized blob.
     * @param shaVal is a SHA-1 value
     * @return content of file which the indicated blob points to
     */
    public String blobFromFile(String shaVal) {
        File blobFile = Utils.join(object, shaVal);
        if (!blobFile.exists()) {
            throw new IllegalArgumentException(
                    "No blob file with that name found.");
        }
        return Utils.readContentsAsString(blobFile);
    }

    /** Takes in SHA-1 and outputs file content of deserialized commit.
     * @param shaVal is a SHA-1 value
     * @return commit from file
     */
    public Commit commitFromFile(String shaVal) {
        File commitFile = Utils.join(object, shaVal);
        if (!commitFile.exists()) {
            throw new IllegalArgumentException(
                    "No commit file with that name found.");
        }
        return Utils.readObject(commitFile, Commit.class);
    }

    /** Takes in and deserializes branch name and return corresponding branch.
     * @return branch with name
     * @param name name of branch
     */
    public Branch branchFromFile(String name) {
        File branchFile = Utils.join(branch, name);
        if (!branchFile.exists()) {
            throw new IllegalArgumentException(
                    "No branch file with that name found.");
        }
        return Utils.readObject(branchFile, Branch.class);
    }

    /** Takes in SHA-1 and outputs file content of deserialized history.
     * @return history from file
     */
    public History historyFromFile() {
        File stageFile = Utils.join(history, "current");
        if (!stageFile.exists()) {
            throw new IllegalArgumentException(
                    "No history file.");
        }
        return Utils.readObject(stageFile, History.class);
    }

    /** Takes in and deserializes a stage of branch b.
     * @param name is a branch
     * @return stage
     */
    public Stage stageFromFile(String name) {
        File stageFile = Utils.join(stage, name);
        if (!stageFile.exists()) {
            throw new IllegalArgumentException(
                    "No stage file with that branch found.");
        }
        return Utils.readObject(stageFile, Stage.class);
    }


    /** Reads in and deserializes a remote from NAME.
     * @param name name
     * @return The remote from file. */
    public static Remote fromFile(String name) {
        if (!Utils.join(Main.REMOTE, name).exists()) {
            throw new IllegalArgumentException(
                    "No remote file with that name found.");
        }
        return Utils.readObject(Utils.join(Main.REMOTE, name), Remote.class);
    }

    /** Save a remote. */
    public void saveRemote() throws IOException {
        Utils.join(Main.REMOTE, _name).createNewFile();
        Utils.writeObject(Utils.join(Main.REMOTE, _name), this);
    }

    /**
     * Checks out all the files tracked by the given FILEMAP.
     * Removes tracked files that
     * are not present in that commit.
     */
    public void reset(HashMap<String, String> fileMap) throws IOException {
        if (cwd.list() != null) {
            for (File file : cwd.listFiles()) {
                if (!file.isHidden()
                        && !file.getName().equals("Makefile")
                        && !file.getName().endsWith(".iml")) {
                    file.delete();
                }
            }
        }
        for (String name : fileMap.keySet()) {
            Utils.join(cwd, name).createNewFile();
            Utils.writeContents(Utils.join(cwd,
                    name), fileMap.get(name));
        }
    }
    /** remote name.
     * @return name . */
    public String getName() {
        return _name;
    }

    /** Return repo.
     * @return repo directory. */
    public File getRepo() {
        return repo;
    }
    /** Return object.
     * @return current object. */
    public File getObject() {
        return object;
    }
    /** Return history.
     * @return current history. */
    public File getHistory() {
        return history;
    }
    /** Return branch.
     * @return branch of remote. */
    public File getBranch() {
        return branch;
    }

    /** Name of the remote. */
    private String _name;
}
