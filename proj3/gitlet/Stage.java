package gitlet;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

/** Class Stage, the staging area.
 * @author Aayush Sutaria
 */
public class Stage implements Serializable {
    /** Stage constructor.
     * @param b is the branch which stage is under */
    public Stage(String b) {
        _name = b.replace("/", "-");
        _trackedFiles = new HashMap<>();
        _stagingTree = new LinkedList<>();
    }
    /** Copy stage s.
     * @param s stage */
    public void copyStage(Stage s) {
        _trackedFiles = s.getTrackedFiles();
        _stagingTree = s.getStagingTree();
    }
    /** Returns if _stagingTree is not empty. */
    public boolean hasStagingTree() {
        return _stagingTree != null && !_stagingTree.isEmpty();
    }

    /** Update tracked files. */
    public void update() {
        _trackedFiles.clear();
        _trackedFiles.putAll(_stagingTree.get(0).getTracked());
    }

    /** Add blob to staging tree which is waiting for commitment.
     * @param blob blob */
    public void addBlob(Blob blob) throws IOException {
        if (!hasStagingTree()) {
            Tree cur = new Tree();
            cur.setTracked(_trackedFiles);
            _stagingTree.add(cur);
        }
        _stagingTree.get(0).addBlob(blob);
        update();
    }

    /** Mark file with "name" for removal.
     * @param name of file to mark */
    public void remove(String name) {
        if (!hasStagingTree()) {
            Tree currentTree = new Tree();
            currentTree.setTracked(_trackedFiles);
            _stagingTree.add(currentTree);
        }
        _stagingTree.get(0).remove(name);
        update();
    }
    /** Unstage file with "name" if staged.
     * @param name file to unstage */
    public void unStage(String name) {
        if (hasStagingTree()) {
            _stagingTree.get(0).unStage(name);
            update();
        }
    }

    /** If tracked files have all been staged, returns true. */
    public boolean allStaged() {
        if (!hasStagingTree()) {
            return true;
        }
        return _stagingTree.get(0).allStaged();
    }

    /** commit then unstage files. */
    public void commit() {
        if (hasStagingTree()) {
            _stagingTree.get(0).commit();
            update();
        }
    }
    /** Clear tree. */
    public void clear() {
        _stagingTree.clear();
    }

    /** Save stage to a file. */
    public void saveStage() throws IOException {
        saveStage(_name);
    }

    /** Save stage as a branch to a file.
     * @param b is a branch */
    public void saveStage(String b) throws IOException {
        Utils.join(Main.STAGE, b).createNewFile();
        _name = b;
        Utils.writeObject(Utils.join(Main.STAGE, b), this);
    }

    /** Takes in and deserializes a stage of branch b.
     * @param b is a branch
     * @return stage
     */
    public static Stage fromFile(String b) {
        if (!Utils.join(Main.STAGE, b.replace("/", "-")).exists()) {
            throw new IllegalArgumentException(
                    "No stage of branch with this name found.");
        }
        return Utils.readObject(Utils.join(Main.STAGE,
                b.replace("/", "-")), Stage.class);
    }

    /** Return hashmap of of staged files. key:file name
     *  and value:SHA-1 val.  */
    public HashMap<String, String> getStaged() {
        return _stagingTree.get(0).getStaged();
    }
    /** Return the SortedSet of staged files' names.  */
    public Set<String> getStagedName() {
        return _stagingTree.get(0).getStagedName();
    }

    /** Return Staging area of the branch with _name. */
    public String getName() {
        return _name;
    }
    /** Return HashMap of tracked files Key:name, value:sha-1 val. */
    public HashMap<String, String> getTrackedFiles() {
        return _trackedFiles;
    }
    /** Return HashMap of tracked files Key:name, value:sha-1 val. */
    public LinkedList<Tree> getStagingTree() {
        return _stagingTree;
    }

    /** Return the SortedSet of files to be removed. */
    public Set<String> getRemove() {
        return _stagingTree.get(0).getRemove();
    }

    /** Tree to be commited.
     * @return tree */
    public Tree getTree() {
        return hasStagingTree() ? _stagingTree.get(0) : null;
    }
    /** Staging area of the branch with _name. */
    private String _name;
    /** HashMap of tracked files Key:name, value:sha-1 val. */
    private HashMap<String, String> _trackedFiles;
    /** Tree in staging area.  */
    private LinkedList<Tree> _stagingTree;
}

