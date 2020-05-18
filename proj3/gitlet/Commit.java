package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

/** Commit class which points to a tree.
 * @author Aayush Sutaria
 */

public class Commit implements Serializable {
    /** Constructor for commit with input of a Tree tree and Message msg.
     * @param tree tree input
     * @param msg input message */
    public Commit(Tree tree, String msg) {
        if (tree == null) {
            _tree = new Tree();
            _treeShaVal = _tree.getTreeShaVal();
            _blobFiles = new HashMap<>();
        } else {
            _tree = tree;
            _treeShaVal = _tree.getTreeShaVal();
            _blobFiles = tree.getTracked();
        }
        ZonedDateTime timeStamp = ZonedDateTime.now();
        DateTimeFormatter pattern
                = DateTimeFormatter.ofPattern("EEE LLL d HH:mm:ss y Z");
        _timeStamp = timeStamp.format(pattern);
        _commitShaVal = Utils.sha1(_treeShaVal, msg);
        _allParents = new HashSet<>();
        _msg = msg;
    }

    /** Returns true if the current commit is merged. */
    public boolean isMerged() {
        return _merged;
    }

    /** Set merge state to true. */
    public void merge() {
        _merged = true;
    }

    /** Returns if there are any tracked files in the current commit. */
    public boolean hasFiles() {
        return _blobFiles != null && _blobFiles.isEmpty();
    }

    /** Sha-1 val of commit.
     * @return sha val */
    public String getCommitShaVal() {
        return _commitShaVal;
    }

    /** Return Commit's Parent. */
    public Commit getParent() {
        return _firstParent;
    }

    /** Set Commit's Parent.
     * @param c commit */
    public void setParent(Commit c) {
        _firstParent = c;
    }

    /** Return Commit's Parent's parent. */
    public Commit getParentsParent() {
        return _firstParentsParent;
    }

    /** Sets Commit's Parent's parent.
     * @param c commit */
    public void setParentsParent(Commit c) {
        _firstParentsParent = c;
    }

    /** Retrun tree.
     * @return tree */
    public Tree getTree() {
        return _tree;
    }
    /** Return Hashmap of blobs with key:file name and value:SHA-1 val. */
    public HashMap<String, String> getBlobFiles() {
        return _blobFiles;
    }

    /** Return Commit Message. */
    public String getMsg() {
        return _msg;
    }

    /** Return Commit timestamp. */
    public String getTimeStamp() {
        return _timeStamp;
    }
    /** Return Commit's first 7 digits of sha1 val. */
    public String getId() {
        return getCommitShaVal().substring(0, 7);
    }


    /** Return true if Commit c is an ancestor of the current commit.
     * @param c commit */
    public boolean isAncestor(String c) {
        return _allParents.contains(c);
    }

    /** Add a single ancestor to the current commit.
     * @param parent is an ancestor */
    public void addAncestor(String parent) {
        _allParents.add(parent);
    }

    /** Add a collection of ancestors to the current commit.
     * @param parents are ancestors */
    public void addAncestors(Collection<String> parents) {
        _allParents.addAll(parents);
    }

    /** Return ancestors of commit. */
    public HashSet<String> getAncestors() {
        return _allParents;
    }

    /** Save Commit to a file. */
    public void saveCommit() throws IOException {
        Utils.join(Main.OBJECT, getCommitShaVal()).createNewFile();
        Utils.writeObject(Utils.join(Main.OBJECT, getCommitShaVal()), this);
    }

    /** Takes in SHA-1 and outputs file content of deserialized commit.
     * @param shaVal is a SHA-1 value
     * @return commit from file
     */
    public static Commit fromFile(String shaVal) {
        File f = Utils.join(Main.OBJECT, shaVal);
        if (!f.exists()) {
            throw new IllegalArgumentException(
                    "No commit file with that name found.");
        }
        return Utils.readObject(f, Commit.class);
    }

    /** Tree of the current commit. */
    private Tree _tree;
    /** SHA-1 val for the tree of the current commit. */
    private String _treeShaVal;
    /** Hashmap of blobs with key:file name and value:SHA-1 val. */
    private HashMap<String, String> _blobFiles;
    /** HashSet of all SHA-1 vals for the ancestors of the current commit. */
    private HashSet<String> _allParents;
    /** Message of the current commit. */
    private String _msg;
    /** Time Stamp of the time that the current commit was committed. */
    private String _timeStamp;
    /** SHA-1 val for the current commit. */
    private String _commitShaVal;
    /** Parent of the current commit. */
    private Commit _firstParent;
    /** Parent of the current commit's parent. */
    private Commit _firstParentsParent;
    /** Merge state. */
    private boolean _merged;
}
