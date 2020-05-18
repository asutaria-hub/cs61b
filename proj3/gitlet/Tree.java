package gitlet;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

/** Tree Class which points to specific files using Blobs!
 * @author Aayush Sutaria
 */

public class Tree implements Serializable {
    /** Tree constructor. */
    public Tree() {
        _trackedFiles = new HashMap<>();
        _stagedFiles = new HashMap<>();
        _stagedFilesName = new TreeSet<>();
        _removeSet = new TreeSet<>();
        _treeShaVal = "no tree";
    }
    /** Add blob to tree which is waiting for commitment.
     * @param blob blob */
    public void addBlob(Blob blob) throws IOException {
        blob.saveContent();
        _stagedFilesName.add(blob.getName());
        _stagedFiles.put(blob.getName(), blob.getShaVal());

    }
    /** Unstage file with "name" if in tree.
     * @param name fileName */
    public void unStage(String name) {
        _stagedFilesName.remove(name);
        _stagedFiles.remove(name);
    }
    /** Mark file with "name" for removal.
     * @param name fileName */
    public void remove(String name) {
        _removeSet.add(name);
        _trackedFiles.remove(name);
    }

    /** If tracked files have all been staged, returns true. */
    public boolean allStaged() {
        return _stagedFiles.isEmpty() && _removeSet.isEmpty();
    }

    /** commit then unstage files. */
    public void commit() {
        _trackedFiles.putAll(_stagedFiles);
        Object [] shaVal = _trackedFiles.keySet().toArray();
        _treeShaVal = Utils.sha1(shaVal);
        _removeSet.clear();
        _stagedFilesName.clear();
        _stagedFiles.clear();
    }


    /** Save tree to a file. */
    public void saveTree() throws IOException {
        Utils.join(Main.OBJECT, getTreeShaVal()).createNewFile();
        Utils.writeObject(Utils.join(Main.OBJECT, getTreeShaVal()), this);
    }

    /** Return hashmap of of tracked files.
     * Key:file name and value:SHA-1 val.  */
    public HashMap<String, String> getTracked() {
        return _trackedFiles;
    }

    /** Return hashmap of of staged files. ey:file name and value:SHA-1 val.  */
    public HashMap<String, String> getStaged() {
        return _stagedFiles;
    }
    /** Return the SortedSet of staged files' names.  */
    public SortedSet<String> getStagedName() {
        return _stagedFilesName;
    }

    /** Return the SortedSet of files to be removed. */
    public SortedSet<String> getRemove() {
        return _removeSet;
    }

    /** Return SHA-1 val of tree.  */
    public String getTreeShaVal() {
        return _treeShaVal;
    }

    /** Set tracked files of the tree.
     * @param tracked files */
    public void setTracked(HashMap<String, String> tracked) {
        _trackedFiles.putAll(tracked);
    }

    /** Hashmap of tracked files with key:file name and value:SHA-1 val. */
    private HashMap<String, String> _trackedFiles;
    /** Hashmap of staged files. ey:file name and value:SHA-1 val. */
    private HashMap<String, String> _stagedFiles;
    /** Sortedset of staged files' names. */
    private SortedSet<String> _stagedFilesName;
    /** Sortedset of files to be removed. */
    private SortedSet<String> _removeSet;
    /** SHA-1 val for this tree. */
    private String _treeShaVal;

}
