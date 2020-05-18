package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Aayush Sutaria
 */
public class Main {
    /** Current Working Directory. */
    static final File CWD = new File(".");
    /** repo directory. */
    static final File REPO = new File("./.gitlet");
    /** object directory. */
    static final File OBJECT = new File(".gitlet/object");
    /** branch directory. */
    static final File BRANCH = new File(".gitlet/branch");
    /** current branch file. */
    static final File CURRENT_BRANCH = new File(".gitlet/current-branch");
    /** staging area directory. */
    static final File STAGE = new File(".gitlet/stage");
    /** commit history file. */
    static final File HISTORY = new File(".gitlet/history");
    /**  remote directory. */
    static final File REMOTE = new File(".gitlet/remote");
    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) throws IOException {
        if (args.length == 0) {
            error("Please enter a command.");
        }
        formatArg(args[0], args);
        if (!args[0].equals("init")) {
            if (!REPO.exists()) {
                error("Not in an initialized Gitlet directory.");
            }
            _branch = Branch.fromFile();
            _stagingArea = Stage.fromFile(_branch.getName());
            _history = History.fromFile(_branch.getName());
            switchHelper(args);

        } else {
            init();
        }
        _history.saveHistory();
        saveCurBranch();
        _stagingArea.saveStage();
    }
    /** Save cur branch name into a file. */
    private static void saveCurBranch() throws IOException {
        CURRENT_BRANCH.createNewFile();
        Utils.writeContents(CURRENT_BRANCH, _branch.getName());
        _branch.saveBranch();
    }

    /** Helper method to allow for intake of specific commands.
     * @param args args user inputted */
    private static void switchHelper(String[] args) throws IOException {
        switch (args[0]) {
        case "add":
            add(args);
            break;
        case "commit":
            commit(args);
            break;
        case "checkout":
            checkout(args);
            break;
        case "log":
            log();
            break;
        case "rm":
            rm(args);
            break;
        case "global-log":
            gLog();
            break;
        case "find":
            find(args);
            break;
        case "status":
            status();
            break;
        case "branch":
            branch(args);
            break;
        case "rm-branch":
            rmBranch(args);
            break;
        case "reset":
            reset(args);
            break;
        case "merge":
            merge(args);
            break;
        case "add-remote":
            addRemote(args);
            break;
        case "rm-remote":
            rmRemote(args);
            break;
        case "push":
            push(args);
            break;
        case "fetch":
            fetch(args);
            break;
        case "pull":
            pull(args);
            break;

        default:
            error("No command with that name exists.");
        }
    }


    /** Prints error message and exits system.
     *@param m is the error message to output
     */
    public static void error(String m) {
        System.out.println(m);
        System.exit(0);
    }

    /** Validates formatting of inputted args based on command.
     * @param command is the inputted command
     * @param args is the args inputted by user from main method
     */
    public static void formatArg(String command, String[] args) {
        boolean valid;
        switch (command) {
        case "init":
        case "log":
        case "global-log":
        case "status":
            valid = args.length == 1;
            break;
        case "commit":
            valid = args.length == 1 || args.length == 2;
            break;
        case "checkout":
            valid = args.length == 2
                    || (args.length == 3 && args[1].equals("--"))
                    || (args.length == 4 && args[2].equals("--"));
            break;
        case "push":
        case "fetch":
        case "pull":
            valid = args.length == 3;
            break;
        case "add-remote":
            valid = args.length == 3 && args[2].matches(".+/\\.gitlet");
            break;
        default:
            valid = args.length == 2;
        }
        if (!valid) {
            error("Incorrect operands.");
        }

    }

    /** Creates a new Gitlet version-control system in the current directory.
     * Or handles cases if there is already an existing Gitlet
     * version-control system.
     */
    public static void init() throws IOException {
        if (REPO.exists()) {
            error("A Gitlet version-control system"
                    + " already exists in the current directory.");
        }
        REPO.mkdir();
        OBJECT.mkdir();
        BRANCH.mkdir();
        STAGE.mkdir();
        HISTORY.mkdir();
        REMOTE.mkdir();
        _stagingArea = new Stage("master");
        new Branch("master").saveBranch();
        _branch = Branch.fromFile("master");
        Commit c = new Commit(null, "initial commit");
        _history.addCommit(c);
        _branch.addCommit(c.getCommitShaVal());
    }

    /** Adds a copy of the file as it currently exists to the staging area.
     * @param args args of user */
    private static void add(String[] args) throws IOException {
        if (!Utils.join(CWD, args[1]).exists()) {
            error("File does not exist.");
        }
        boolean remove = false;
        boolean check = true;
        if (_stagingArea.hasStagingTree()) {
            if (_stagingArea.getRemove().contains(args[1])) {
                _stagingArea.getRemove().remove(args[1]);
                remove = true;
            }
        }
        if (!remove) {
            Blob blob = new Blob(args[1]);
            if (!_history.isEmpty()) {
                if (_history.getCommit(_branch.getHead()).getBlobFiles()
                        != null) {
                    if (_history.getCommit(_branch.getHead()).
                            getBlobFiles().containsKey(args[1])) {
                        if (_history.getCommit(_branch.getHead()).
                                getBlobFiles().get(args[1]).equals
                                (blob.getShaVal())) {
                            _stagingArea.unStage(args[1]);
                            check = false;
                        }
                    }
                }
            }
            if (check) {
                _stagingArea.addBlob(blob);
            }
        }
    }
    /** Saves a snapshot of certain files in the current commit and staging area
     *  so they can be restored at a later time, creating a new commit.
     *  @param args args of user */
    private static void commit(String[] args) throws IOException {
        if (_stagingArea.allStaged()) {
            error("No changes added to the commit.");
        }
        if (args.length == 1 || (args.length == 2
                && args[1].trim().isEmpty())) {
            error("Please enter a commit message.");
        }
        _stagingArea.commit();
        Commit c = new Commit(_stagingArea.getTree(), args[1]);
        _stagingArea.clear();
        c.addAncestors(_branch.getCommits());
        if (!_history.isEmpty()) {
            c.setParent(_history.getCommit(_branch.getHead()));
        }
        _history.addCommit(c);
        _branch.addCommit(c.getCommitShaVal());
    }
    /** Starting at the current head commit, display information
     *  about each commit backwards along the commit tree until
     *  the initial commit, following the first parent commit links,
     *  ignoring any second parents found in merge commits.
     *  */
    private static void log() {
        Commit c = _history.getCommit(_branch.getHead());
        while (c != null) {
            System.out.println("===\ncommit " + c.getCommitShaVal());
            if (c.isMerged()) {
                System.out.println("Merge: " + c.getParent().getId()
                        + " " + c.getParentsParent().getId());
            }
            System.out.println("Date: "
                    + c.getTimeStamp()
                    + "\n" + c.getMsg());
            System.out.println();
            c = c.getParent();
        }
    }
    /** General command with different functions depending on args.
     * @param args args of user */
    public static void checkout(String[] args) throws IOException {
        if (args.length == 3) {
            if (!_history.getCommit(_branch.getHead()).
                    getBlobFiles().containsKey(args[2])) {
                error("File does not exist in that commit.");
            }
            Utils.join(CWD, args[2]).createNewFile();
            Utils.writeContents(Utils.join(CWD, args[2]),
                    Blob.fromFile(_history.getCommit(_branch.getHead()).
                            getBlobFiles().get(args[2])));
        } else if (args.length == 4) {
            Commit c = null;
            for (String id : OBJECT.list()) {
                if (id.startsWith(args[1])) {
                    c = Commit.fromFile(id);
                    break;
                }
            }
            if (c == null) {
                error("No commit with that id exists.");
            }
            if (!c.getBlobFiles().containsKey(args[3])) {
                error("File does not exist in that commit.");
            }
            Utils.join(CWD, args[3]).createNewFile();
            Utils.writeContents(Utils.join(CWD, args[3]),
                    Blob.fromFile(c.getBlobFiles().get(args[3])));
        } else {
            helperCheckout(args);
        }
    }
    /** Handles a case for checkout to keep
     *  checkout method shorter to satisfy style check.
     *@param args args */
    private static void helperCheckout(String[] args) throws IOException {
        String name = args[1].replace("/", "-");
        if (!Arrays.asList(BRANCH.list()).contains(name)) {
            error("No such branch exists.");
        } else if (_branch.getName().equals(name)) {
            error("No need to checkout the current branch.");
        } else {
            if (CURRENT_BRANCH.list() != null) {
                for (String f : CWD.list()) {
                    if (!f.startsWith(".")) {
                        if (unTracked(f)) {
                            error("There is an untracked file in the w"
                                    + "ay; delete it or add it first.");
                        }
                    }
                }
            }
            HashMap<String, String> files =
                    Commit.fromFile(Branch.fromFile(name)
                            .getHead()).getBlobFiles();
            if (files != null) {
                for (String str : files.keySet()) {
                    Utils.join(CWD, str).createNewFile();
                    Utils.writeContents(Utils.join(CWD,
                            str), Blob.fromFile(files.get(str)));
                }
            }
            for (File f : CWD.listFiles()) {
                if (!f.isHidden()) {
                    if (!f.isDirectory()) {
                        if (!f.getName().equals("Makefile")) {
                            if (!f.getName().endsWith(".iml")) {
                                if (!files.containsKey(f.getName())) {
                                    if (_stagingArea.getTrackedFiles()
                                            != null) {
                                        if (_stagingArea.getTrackedFiles().
                                                containsKey(f.getName())) {
                                            f.delete();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            _branch = Branch.fromFile(name);
        }
        _stagingArea.clear();
    }

    /** Unstage the file if it is currently staged for addition.
     * @param args args inputted
     */
    public static void rm(String[] args) {
        int removed = 0;
        if (_stagingArea.hasStagingTree()
                && _stagingArea.getTree().
                getStaged().containsKey(args[1])) {
            removed++;
            _stagingArea.unStage(args[1]);
        }
        if (Commit.fromFile(_branch.getHead()).getBlobFiles().
                containsKey(args[1])) {
            removed++;
            _stagingArea.remove(args[1]);
            Utils.restrictedDelete(Utils.join(CWD, args[1]));
        }
        if (removed == 0) {
            error("No reason to remove the file.");
        }
    }
        /** Global log is Like log, except displays information about
         * all commits ever made. The order of the commits does not matter.
         * */
    public static void gLog() {
        for (Commit c : _history.getCommitHistory().values()) {
            System.out.println("===\ncommit "
                    + c.getCommitShaVal());
            if (c.isMerged()) {
                System.out.println("Merge: " + c.getParent().getId() + " "
                        + c.getParentsParent().getId());
            }
            System.out.println("Date: "
                    + c.getTimeStamp() + "\n" + c.getMsg());
            System.out.println();
        }
    }
    /** Prints out the ids of all commits that
     * have the given commit message, one per line.
     *@param args args */
    public static void find(String[] args) {
        int count = 0;
        for (Commit c : _history.getCommitHistory().values()) {
            if (c.getMsg().contains(args[1])) {
                System.out.println(c.getCommitShaVal());
                count++;
            }
        }
        if (count == 0) {
            error("Found no commit with that message.");
        }
    }
    /**  Creates a new branch with the given name,
     *  and points it at the current head node.
     * @param args args inputted */
    public static void branch(String[] args) throws IOException {
        if (!Arrays.asList(BRANCH.list()).contains(args[1])) {
            new Branch(args[1], _branch.getHead()).saveBranch();
            Branch.fromFile(args[1]).addCommits(_branch.getCommits());
            _stagingArea.saveStage(args[1]);
        } else {
            error("A branch with that name already exists.");
        }
    }
    /** Deletes the branch with the given name.
     * @param args args inputted */
    public static void rmBranch(String[] args) {
        if (!Arrays.asList(BRANCH.list()).contains(args[1])) {
            error("A branch with that name does not exist.");
        } else if (_branch.getName().equals(args[1])) {
            error("Cannot remove the current branch.");
        } else {
            Utils.join(BRANCH, args[1]).delete();
            Utils.join(STAGE, args[1]).delete();
        }
    }
    /**Checks out all the files tracked by the given commit.
     * @param args input */
    public static void reset(String[] args) throws IOException {
        Commit c = null;
        int find = 0;
        for (String shaVal : OBJECT.list()) {
            if (shaVal.startsWith(args[1])) {
                find++;
                c = Commit.fromFile(shaVal);
                break;
            }
        }
        if (find != 0) {
            if (CWD.list() != null) {
                for (String f : CWD.list()) {
                    if (!f.startsWith(".")) {
                        if (!_stagingArea.getTrackedFiles().containsKey(f)) {
                            if (c.hasFiles()) {
                                if (c.getBlobFiles().containsKey(f)) {
                                    if (!Blob.fromFile(c.getBlobFiles().get(f))
                                            .equals(Utils.readContentsAsString
                                                    (Utils.join(CWD, f)))) {
                                        error("There is an untracked file "
                                                + "in the way; delete"
                                                + " it or add it first.");
                                    }
                                }
                            }
                        }
                    }
                }
                for (File file : CWD.listFiles()) {
                    if (!file.isHidden()) {
                        if (!file.getName().equals("Makefile")) {
                            if (!file.getName().endsWith(".iml")) {
                                file.delete();
                            }
                        }
                    }
                }
            }
            HashMap<String, String> files = c.getBlobFiles();
            for (String fileName : files.keySet()) {
                Utils.writeContents(Utils.join(CWD, fileName),
                        Blob.fromFile(files.get(fileName)));
            }
        } else {
            error("No commit with that id exists.");
        }
        _stagingArea.clear();
        _history.addCommit(c);
        _branch.addCommit(c.getCommitShaVal());
    }
    /**  Displays what branches currently exist,
     *  and marks the current branch with a *.
     *  Also displays what files have been staged for addition or removal. */
    public static void status() {
        System.out.println("=== Branches ===");
        Stack<String> otherBranches = new Stack<>();
        for (String b : BRANCH.list()) {
            if (!b.equals(_branch.getName())) {
                otherBranches.push(b);
            } else {
                System.out.println("*" + b);
            }
        }

        while (!otherBranches.isEmpty()) {
            System.out.println(otherBranches.pop());
        }

        System.out.println("\n=== Staged Files ===");
        if (!_stagingArea.allStaged()) {
            for (String fileName : _stagingArea.getStagedName()) {
                System.out.println(fileName);
            }
        }
        System.out.println("\n=== Removed Files ===");
        if (_stagingArea.hasStagingTree()) {
            if (_stagingArea.getRemove() != null) {
                if (!_stagingArea.getRemove().isEmpty()) {
                    for (String fileName : _stagingArea.getRemove()) {
                        System.out.println(fileName);
                    }
                }
            }
        }
        System.out.println("\n=== Modifications Not Staged For Commit ===");
        HashSet<String> fileName = new HashSet<>();
        if (CWD.list() != null) {
            fileName.addAll(Arrays.asList(CWD.list()));
        }
        fileName.addAll(_stagingArea.getTrackedFiles().keySet());
        if (_stagingArea.hasStagingTree()) {
            fileName.addAll(_stagingArea.getStagedName());
        }
        stausHelper(fileName);
        System.out.println("\n=== Untracked Files ===");
        if (CWD.list() != null) {
            for (String name : CWD.list()) {
                if (unTracked(name)) {
                    System.out.println(name);
                }
            }
        }
    }

    /** Helper function to pass style check.
     * @param file file */
    private static void stausHelper(HashSet<String> file) {
        for (String f : file) {
            if (!Utils.join(CWD, f).exists()
                    || (Utils.join(CWD, f).exists()
                    && Utils.join(CWD, f).isFile())) {
                if (!Utils.join(CWD, f).getName().startsWith(".")) {
                    if (!Utils.join(CWD, f).getName().equals("Makefile")) {
                        if (!Utils.join(CWD, f).getName().
                                endsWith(".iml")) {
                            if (situation(Utils.join(CWD, f)) != null) {
                                if (situation(Utils.join(CWD, f))
                                        .equals("deleted")) {
                                    System.out.println(f + " (deleted)");
                                } else if (situation((Utils.join(CWD, f)))
                                        .equals("modified")) {
                                    System.out.println(f + " (modified)");
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    /** Helper function for status to return if file is untracked.
     * @param fileName is the file name */
    public static boolean unTracked(String fileName) {
        boolean unTrack = false;
        if (!_history.getCommit(_branch.getHead()).
                getBlobFiles().containsKey(fileName)) {
            if (_stagingArea.allStaged()
                    || (!_stagingArea.allStaged()
                    && !_stagingArea.getStagedName().contains(fileName))) {
                if (!fileName.startsWith(".")) {
                    if (!fileName.endsWith(".iml")) {
                        if (!fileName.equals("Makefile")) {
                            if (!Utils.join(CWD, fileName).isDirectory()) {
                                unTrack = true;
                            }
                        }
                    }
                }
            }
        }
        return unTrack;
    }


    /** Helper method for status, return file situation.
     * @param f file*/
    public static String situation(File f) {
        if (!f.exists()) {
            if (!_stagingArea.allStaged()) {
                if (_stagingArea.getStaged().containsKey(f.getName())) {
                    return "deleted";
                }
            }
        }
        if (_history.getCommit(_branch.getHead()).getBlobFiles().
                containsKey(f.getName())) {
            if (!f.exists()) {
                if (!_stagingArea.hasStagingTree()
                        || !_stagingArea.getRemove().contains(f.getName())) {
                    return "deleted";
                }
            }
        }
        if (f.exists()) {
            Blob blob = new Blob(f.getName());
            if ((_stagingArea.allStaged()
                    || (!_stagingArea.allStaged()
                    && !_stagingArea.getStagedName().contains(f.getName())
                    && !_stagingArea.getRemove().contains(f.getName())))) {
                if (_history.getCommit(_branch.getHead()).
                        getBlobFiles().containsKey(f.getName())) {
                    if (!_history.getCommit(_branch.getHead()).
                            getBlobFiles().get(f.getName()).
                            equals(blob.getShaVal())) {
                        return "modified";
                    }
                }
            }

            if (!_stagingArea.allStaged()) {
                if (_stagingArea.getStaged().containsKey(f.getName())) {
                    if (!_stagingArea.getStaged().
                            get(f.getName()).equals(blob.getShaVal())) {
                        return "modified";
                    }
                }
            }
        }
        return null;
    }

    /**
     * Merges files from the given branch into the current branch.
     * @param args args
     */
    public static void merge(String[] args) throws IOException {
        String name = args[1].replace("/", "-");
        if (_stagingArea.hasStagingTree()) {
            error("You have uncommitted changes.");
        } else if (!Arrays.asList(BRANCH.list()).contains(name)) {
            error("A branch with that name does not exist.");
        } else if (_branch.getName().equals(name)) {
            error("Cannot merge a branch with itself");
        } else {
            Branch branchToMerge = Branch.fromFile(name);
            String splitPoint = determineSp(branchToMerge);
            if (_branch.getHead().equals(splitPoint)) {
                _branch.copyBranch(branchToMerge);
                saveCurBranch();
                _stagingArea.copyStage(Stage.fromFile(branchToMerge.getName()));
                _stagingArea.saveStage();
                reFile(Commit.fromFile(branchToMerge.getHead()));
                error("Current branch fast-forwarded.");
            } else if (branchToMerge.getHead().equals(splitPoint)) {
                error("Given branch is an "
                        + "ancestor of the current branch.");
            } else if (completeMerge(name, splitPoint)) {
                System.out.println("Encountered a merge conflict.");
            }
        }
    }

    /** Help method for merge. Update and refile files in the current working
     * directory according to the COMMIT.
     */
    public static void reFile(Commit commit) throws IOException {
        for (File file : CWD.listFiles()) {
            if (!file.isHidden()
                    && !file.getName().equals("Makefile")
                    && !file.getName().endsWith(".iml")) {
                file.delete();
            }
        }
        for (String fileName : commit.getBlobFiles().keySet()) {
            Utils.join(CWD, fileName).createNewFile();
            Utils.writeContents(Utils.join(CWD, fileName),
                    Blob.fromFile(commit.getBlobFiles().get(fileName)));
        }
    }

    /** Help method for merge. Find the split point with
     * this branch and the branch to be merged
     * @param branchToMerge branch
     * @return  The sha1 code of split point commit.
     */
    public static String determineSp(Branch branchToMerge) {
        String splitPoint = _branch.getCommits().getFirst();
        int minDistance = Integer.MAX_VALUE;
        int index = 1;
        while (index < Math.min(_branch.getCommitSize(),
                branchToMerge.getCommitSize())) {
            if (branchToMerge.getCommit(index).
                    equals(_branch.getCommit(index))) {
                splitPoint =  _branch.getCommit(index);
            } else {
                int currentCommitDistance, mergeCommitDistance;
                if (Commit.fromFile(branchToMerge.getHead()).isAncestor(
                        _branch.getCommit(index))) {
                    currentCommitDistance = distance(_branch.
                            getCommit(index), _branch.getHead(), branchToMerge);
                    if (currentCommitDistance < minDistance) {
                        splitPoint = _branch.getCommit(index);
                        minDistance = currentCommitDistance;
                    }
                }
                if (Commit.fromFile(_branch.getHead()).isAncestor(
                        branchToMerge.getCommit(index))) {
                    mergeCommitDistance = distance(branchToMerge.
                            getCommit(index), _branch.getHead(), branchToMerge);
                    if (mergeCommitDistance < minDistance) {
                        splitPoint = branchToMerge
                                .getCommit(index);
                        minDistance = mergeCommitDistance;
                    }
                }
            }
            index++;
        }
        return splitPoint;
    }

    /** Help method for merge to find distance from file to file.
     * @param branchToMerge b
     * @param from from
     * @param to to
     * @return  int */
    public static int distance(String from, String to, Branch branchToMerge) {
        if (_branch.hasCommit(to)) {
            if (_branch.hasCommit(from)) {
                if (_branch.getCommits().indexOf(to)
                        < _branch.getCommits().
                        indexOf(from)) {
                    return _branch.getCommitSize()
                            + branchToMerge.getCommitSize();
                } else {
                    return _branch.getCommits().indexOf(to)
                            - _branch.getCommits().indexOf(from);
                }
            } else  {
                if (Commit.fromFile(to).getParentsParent() != null) {
                    return 1 + distance(from,
                            Commit.fromFile(to).getParentsParent().
                                    getCommitShaVal(), branchToMerge);
                } else {
                    return 1 + distance(from,
                            Commit.fromFile(to).getParent().
                                    getCommitShaVal(), branchToMerge);
                }
            }
        } else  {
            if (branchToMerge.hasCommit(from)) {
                if (branchToMerge.getCommits().
                        indexOf(to) < branchToMerge.
                        getCommits().indexOf(from)) {
                    return _branch.getCommitSize()
                            + branchToMerge.getCommitSize();
                } else {
                    return branchToMerge.getCommits().indexOf(to)
                            - branchToMerge.getCommits().indexOf(from);
                }
            } else  {
                if (Commit.fromFile(to).getParentsParent() != null) {
                    return 1 + distance(from,
                            Commit.fromFile(to).getParentsParent().
                                    getCommitShaVal(), branchToMerge);
                } else {
                    return 1 + distance(from,
                            Commit.fromFile(to).getParent().
                                    getCommitShaVal(), branchToMerge);
                }
            }
        }
    }
    /** Help method for merge.
     * @param gb given branch
     * @param sp splitting point
     * @return Conflict situation.*/
    public static boolean completeMerge(String gb,
                                        String sp) throws IOException {
        Branch branchToMerge = Branch.fromFile(gb);
        HashMap<String, String> mergeFileMap
                = Commit.fromFile(branchToMerge.getHead()).getBlobFiles();
        HashMap<String, String> splitPointFileMap
                = Commit.fromFile(sp).getBlobFiles();
        HashMap<String, String> currentFileMap
                = Commit.fromFile(_branch.getHead()).getBlobFiles();
        HashSet<String> allFileName = new HashSet<>(mergeFileMap.keySet());
        allFileName.addAll(splitPointFileMap.keySet());
        allFileName.addAll(currentFileMap.keySet());
        boolean conflict = false;
        for (String fileName : allFileName) {
            if (!splitPointFileMap.containsKey(fileName)) {
                if (!currentFileMap.containsKey(fileName)
                        && mergeFileMap.containsKey(fileName)) {
                    checkout(new String[]{"checkout",
                            branchToMerge.getHead(), "--", fileName});
                    _stagingArea.addBlob(new Blob(fileName));
                }
                if (currentFileMap.containsKey(fileName)
                        && mergeFileMap.containsKey(fileName)
                        && !currentFileMap.get(fileName).
                        equals(mergeFileMap.get(fileName))) {
                    String content = "<<<<<<< HEAD" + System.lineSeparator()
                            + Blob.fromFile(currentFileMap.get(fileName))
                            + "=======" + System.lineSeparator()
                            + Blob.fromFile(mergeFileMap.
                            get(fileName)) + ">>>>>>>";
                    Utils.writeContents(Utils.join(CWD, fileName), content);
                    conflict = true;
                }
            } else {
                conflict = mergeSplitExist(mergeFileMap,
                        splitPointFileMap, currentFileMap,
                        fileName, conflict);
            }
        }
        _stagingArea.commit();
        Commit tracked = new Commit(_stagingArea.
                getTree(), "Merged " + gb.replace("-", "/")
                + " into " + _branch.getName().replace("-", "/") + ".");
        tracked.merge();
        tracked.setParent(Commit.fromFile(_branch.getHead()));
        tracked.setParentsParent(Commit.fromFile(branchToMerge.getHead()));
        tracked.addAncestors(_branch.getCommits());
        tracked.addAncestor(Commit.fromFile
                (branchToMerge.getHead()).getCommitShaVal());
        tracked.addAncestors(Commit.fromFile
                (branchToMerge.getHead()).getAncestors());
        _history.addCommit(tracked);
        _branch.addCommit(tracked.getCommitShaVal()); _stagingArea.clear();
        helper(tracked);
        return conflict;
    }
    /** helper to satisfy style check.
     * @param tracked tracked */
    private static void helper(Commit tracked) {
        for (File file : CWD.listFiles()) {
            if (tracked.getBlobFiles() != null && !tracked.
                    getBlobFiles().containsKey(file.getName())
                    && !file.isHidden() && !file.isDirectory()
                    && !file.getName().equals("Makefile")
                    && !file.getName().endsWith(".iml")) {
                file.delete();
            }
        }
    }
    /** Help method for merge.
     * @param currentFileMap c
     * @param mergeFileMap m
     * @param fileName f
     * @param splitPointFileMap s
     * @param conflict c
     * @return Conflict situation. True when file merging face conflict.
     */
    public static boolean mergeSplitExist(
            HashMap<String, String> mergeFileMap,
            HashMap<String, String> splitPointFileMap,
            HashMap<String, String> currentFileMap,
            String fileName, boolean conflict
    ) throws IOException {
        if (mergeFileMap.containsKey(fileName)
                && currentFileMap.containsKey(fileName)
                && !splitPointFileMap.get(fileName).
                equals(mergeFileMap.get(fileName))
                && splitPointFileMap.get(fileName).
                equals(currentFileMap.get(fileName))) {
            Utils.writeContents(Utils.join(CWD,
                    fileName), Blob.fromFile(
                    mergeFileMap.get(fileName)));
            _stagingArea.addBlob(new Blob(fileName));
        } else if (!mergeFileMap.containsKey(fileName)
                && currentFileMap.containsKey(fileName)
                && splitPointFileMap.get(fileName).
                equals(currentFileMap.get(fileName))) {
            rm(new String[]{"rm", fileName});
        } else if ((currentFileMap.containsKey(fileName)
                && mergeFileMap.containsKey(fileName)
                && !currentFileMap.get(fileName).
                equals(mergeFileMap.get(fileName))
                && !currentFileMap.get(fileName).
                equals(splitPointFileMap.get(fileName))
                && !mergeFileMap.get(fileName).
                equals(splitPointFileMap.get(fileName)))
                || (!currentFileMap.containsKey(fileName)
                && mergeFileMap.containsKey(fileName)
                && !mergeFileMap.get(fileName).
                equals(splitPointFileMap.get(fileName)))
                || (currentFileMap.containsKey(fileName)
                && !mergeFileMap.containsKey(fileName)
                && !currentFileMap.get(fileName).
                equals(splitPointFileMap.get(fileName)))) {
            String current = currentFileMap.containsKey(fileName)
                    ? System.lineSeparator()
                    + Blob.fromFile(currentFileMap.get(fileName))
                    : System.lineSeparator();
            String merge = mergeFileMap.containsKey(fileName)
                    ? System.lineSeparator()
                    + Blob.fromFile(mergeFileMap.get(fileName))
                    : System.lineSeparator();
            String content = "<<<<<<< HEAD"
                    + current + "======="
                    + merge + ">>>>>>>";
            Utils.join(CWD, fileName).createNewFile();
            Utils.writeContents(Utils.join(CWD, fileName), content);
            _stagingArea.addBlob(new Blob(fileName));
            return true;
        }
        return conflict;
    }

    /**
     * Saves the given login information under the
     * given remote name.
     * @param args input
     */
    public static void addRemote(String[] args) throws IOException {
        if (REMOTE.list() != null
                && Arrays.asList(REMOTE.list()).contains(args[1])) {
            error("A remote with that name already exists.");
        } else {
            new Remote(args[1], args[2]);
        }
    }

    /**
     * Removes the given login information under the
     * given remote name.
     * @param args input
     */
    public static void rmRemote(String[] args) {
        if (!Arrays.asList(REMOTE.list()).contains(args[1])) {
            error("A remote with that name does not exist.");
        } else {
            Utils.join(REMOTE, args[1]).delete();
            Utils.join(HISTORY, args[1]).delete();
            for (File file : STAGE.listFiles()) {
                if (file.getName().contains(args[1] + "-")) {
                    file.delete();
                }
            }
        }
    }

    /**
     * push for remote.
     * @param args args
     */

    public static void push(String[] args) throws IOException {
        Remote  remote = Remote.fromFile(args[1]);
        if (!remote.getRepo().exists()) {
            error("Remote directory not found.");
        } else {
            HashMap<String, String> fileMap = new HashMap<>();
            Commit pushCommit = Commit.fromFile(_branch.getHead());
            History history = remote.historyFromFile();
            Branch branch;
            for (String name : pushCommit.
                    getBlobFiles().keySet()) {
                fileMap.put(name, Blob.fromFile(pushCommit.
                        getBlobFiles().get(name)));
            }
            if (!Utils.join(remote.getBranch(), args[2]).exists()) {
                branch = new Branch(args[2], _branch.getHead());
                Utils.join(remote.getBranch(), args[2]).createNewFile();
            } else {
                branch = remote.branchFromFile(args[2]);
                if (_history.getCommit(branch.getHead()) == null) {
                    error("Please pull "
                            + "down remote changes before pushing.");
                }
            }
            branch.addCommit(pushCommit.getCommitShaVal());
            Utils.join(remote.getObject(), pushCommit.
                    getCommitShaVal()).createNewFile();
            Utils.writeObject(Utils.join(remote.getObject(),
                    pushCommit.getCommitShaVal()), pushCommit);
            remote.reset(fileMap);
            history.addCommit(pushCommit);
            Utils.writeObject(Utils.join(remote.
                    getBranch(), args[2]), branch);
            Utils.writeObject(Utils.join(remote.getHistory(),
                    "current"), history);
        }
    }

    /**
     * fetch repo.
     * @param args args
     */
    public static void fetch(String[] args) throws IOException {
        Remote remote = Remote.fromFile(args[1]);
        if (!remote.getRepo().exists()) {
            error("Remote directory not found.");
        } else if (!Utils.join(remote.getBranch(), args[2]).exists()) {
            error("That remote does not have that branch.");
        } else {
            Branch branch = remote.branchFromFile(args[2]);
            branch.changeName(args[1] + "-" + args[2]);
            branch.saveBranch();
            Commit commit = remote.commitFromFile(branch.getHead());
            fetchHelper(commit, remote);
            Stage stage = remote.stageFromFile(args[2]);
            Utils.join(STAGE, args[1]
                    + "-" + args[2]).createNewFile();
            Utils.writeObject(Utils.join(STAGE,
                    args[1] + "-" + args[2]), stage);
            History history = remote.historyFromFile();
            history.saveHistory(args[1]);
        }
    }

    /** Help method for fetch. Save COMMIT in cwd.
     * @param commit commit
     * @param remote remote*/
    public static void fetchHelper(Commit commit, Remote remote)
            throws IOException {
        Utils.writeObject(Utils.join(OBJECT,
                commit.getCommitShaVal()), commit);
        Utils.writeObject(Utils.join(OBJECT,
                commit.getTree().getTreeShaVal()), commit.getTree());
        for (String fileName : commit.getTree().getTracked().keySet()) {
            String sha1 = commit.getTree().getTracked().get(fileName);
            String content = remote.blobFromFile(sha1);
            Utils.join(OBJECT, sha1).createNewFile();
            Utils.writeContents(Utils.join(OBJECT, sha1), content);
        }
    }

    /**
     * pull repo.
     * @param args input
     */
    public static void pull(String[] args) throws IOException {
        fetch(new String[]{"fetch", args[1], args[2]});
        merge(new String[]{"merge", args[1] + "/" + args[2]});
    }

    /** The stage area of CWD. */
    private static Stage _stagingArea;
    /** The commit history of CWD. */
    private static History _history = new History();
    /** Current branch. */
    private static Branch _branch;
}


