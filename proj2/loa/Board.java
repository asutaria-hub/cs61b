/* Skeleton Copyright (C) 2015, 2020 Paul N. Hilfinger and the Regents of the
 * University of California.  All rights reserved. */
package loa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Formatter;
import java.util.Arrays;

import java.util.regex.Pattern;

import static loa.Piece.*;
import static loa.Square.*;

/** Represents the state of a game of Lines of Action.
 *  @author Aayush Sutaria
 */
class Board {

    /** Default number of moves for each side that results in a draw. */
    static final int DEFAULT_MOVE_LIMIT = 150;

    /** Pattern describing a valid square designator (cr). */
    static final Pattern ROW_COL = Pattern.compile("^[a-h][1-8]$");

    /** A Board whose initial contents are taken from INITIALCONTENTS
     *  and in which the player playing TURN is to move. The resulting
     *  Board has
     *        get(col, row) == INITIALCONTENTS[row][col]
     *  Assumes that PLAYER is not null and INITIALCONTENTS is 8x8.
     *
     *  CAUTION: The natural written notation for arrays initializers puts
     *  the BOTTOM row of INITIALCONTENTS at the top.
     */
    Board(Piece[][] initialContents, Piece turn) {
        initialize(initialContents, turn);
    }

    /** A new board in the standard initial position. */
    Board() {
        this(INITIAL_PIECES, BP);
    }

    /** A Board whose initial contents and state are copied from
     *  BOARD. */
    Board(Board board) {
        this();
        copyFrom(board);
    }

    /** Set my state to CONTENTS with SIDE to move. */
    void initialize(Piece[][] contents, Piece side) {
        _moves.clear();
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                set(sq(c, r), contents[r][c]);
            }
        }
        _turn = side;
        _moveLimit = DEFAULT_MOVE_LIMIT;
    }

    /** Set me to the initial configuration. */
    void clear() {
        initialize(INITIAL_PIECES, BP);
    }

    /** Set my state to a copy of BOARD. */
    void copyFrom(Board board) {
        if (board == this) {
            return;
        }
        _moves.clear();
        _moves.addAll(board._moves);
        _turn = board._turn;
        for (int i = 0; i < board._board.length; i++) {
            this._board[i] = board._board[i];
        }
    }

    /** Return the contents of the square at SQ. */
    Piece get(Square sq) {
        return _board[sq.index()];
    }

    /** Set the square at SQ to V and set the side that is to move next
     *  to NEXT, if NEXT is not null. */
    void set(Square sq, Piece v, Piece next) {
        this._board[sq.index()] = v;
        if (next != null) {
            _turn = next;
        }

    }

    /** Set the square at SQ to V, without modifying the side that
     *  moves next. */
    void set(Square sq, Piece v) {
        set(sq, v, null);
    }

    /** Set limit on number of moves by each side that results in a tie to
     *  LIMIT, where 2 * LIMIT > movesMade(). */
    void setMoveLimit(int limit) {
        if (2 * limit <= movesMade()) {
            throw new IllegalArgumentException("move limit too small");
        }
        _moveLimit = 2 * limit;
    }

    /** Assuming isLegal(MOVE), make MOVE. This function assumes that
     *  MOVE.isCapture() will return false.  If it saves the move for
     *  later retraction, makeMove itself uses MOVE.captureMove() to produce
     *  the capturing move. */
    void makeMove(Move move) {
        assert isLegal(move);
        Piece moved = _board[move.getFrom().index()];
        Piece replaced = _board[move.getTo().index()];
        if (replaced != EMP) {
            move = move.captureMove();
        }
        set(move.getTo(), moved);
        set(move.getFrom(), EMP);
        _turn = _turn.opposite();
        _moves.add(move);
    }

    /** Retract (unmake) one move, returning to the state immediately before
     *  that move.  Requires that movesMade () > 0. */
    void retract() {
        assert movesMade() > 0;
        Move unMove = _moves.remove(_moves.size() - 1);
        Piece moved = _board[unMove.getTo().index()];
        Piece replaced = EMP;
        if (unMove.isCapture()) {
            replaced = moved.opposite();
        }
        set(unMove.getTo(), replaced);
        set(unMove.getFrom(), moved);
        _turn = _turn.opposite();
    }

    /** Return the Piece representing who is next to move. */
    Piece turn() {
        return _turn;
    }

    /** Return true iff FROM - TO is a legal move for the player currently on
     *  move. */
    boolean isLegal(Square from, Square to) {
        if (blocked(from, to)) {
            return false;
        }
        if (numPieces(from, to) != from.distance(to)) {
            return false;
        }

        if (from.moveDest(from.direction(to), from.distance(to)) == null) {
            return false;
        }

        return from.isValidMove(to);
    }

    /** Return true iff MOVE is legal for the player currently on move.
     *  The isCapture() property is ignored. */
    boolean isLegal(Move move) {
        return isLegal(move.getFrom(), move.getTo());
    }

    /** Return a sequence of all legal moves from this position. */
    List<Move> legalMoves() {
        ArrayList<Move> lst = new ArrayList<Move>();
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                Square from = sq(c, r);
                if (_board[from.index()] == _turn) {
                    for (int row = 0; row < BOARD_SIZE; row++) {
                        for (int col = 0; col < BOARD_SIZE; col++) {
                            Square to = sq(col, row);
                            if (isLegal(from, to)) {
                                lst.add(Move.mv(from, to));
                            }
                        }
                    }
                }

            }
        }
        return lst;
    }

    /** Return a sequence of all legal moves from this position for opposite. */
    List<Move> legalMovesOpposite() {
        ArrayList<Move> lst = new ArrayList<Move>();
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                Square from = sq(c, r);
                if (_board[from.index()] == _turn.opposite()) {
                    for (int row = 0; row < BOARD_SIZE; row++) {
                        for (int col = 0; col < BOARD_SIZE; col++) {
                            Square to = sq(col, row);
                            if (isLegal(from, to)) {
                                lst.add(Move.mv(from, to));
                            }
                        }
                    }
                }

            }
        }
        return lst;
    }

    /** Return true iff the game is over (either player has all his
     *  pieces continguous or there is a tie). */
    boolean gameOver() {
        checkWinner();
        return winner() != null;
    }

    /** Return true iff SIDE's pieces are continguous. */
    boolean piecesContiguous(Piece side) {
        int i = getRegionSizes(side).size();
        return i == 1;
    }
    /** check winner. */
    void checkWinner() {
        computeRegions();
        if (!_winnerKnown) {
            if (_moves.size() >= _moveLimit || _whiteRegionSizes.size() == 1
                    || _blackRegionSizes.size() == 1) {
                _winnerKnown = true;
            }
        }
    }

    /** Return the winning side, if any.  If the game is not over, result is
     *  null.  If the game has ended in a tie, returns EMP. */
    Piece winner() {
        if (_winnerKnown) {
            if (_moves.size() >= _moveLimit) {
                return EMP;
            }
            if (_whiteRegionSizes.size() == 1
                    && _blackRegionSizes.size() == 1) {
                return _turn.opposite();
            } else if (_whiteRegionSizes.size() == 1) {
                return WP;
            } else if (_blackRegionSizes.size() == 1) {
                return BP;
            }
        }
        return null;
    }

    /** Return the total number of moves that have been made (and not
     *  retracted).  Each valid call to makeMove with a normal move increases
     *  this number by 1. */
    int movesMade() {
        return _moves.size();
    }

    @Override
    public boolean equals(Object obj) {
        Board b = (Board) obj;
        return Arrays.deepEquals(_board, b._board) && _turn == b._turn;
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(_board) * 2 + _turn.hashCode();
    }

    @Override
    public String toString() {
        Formatter out = new Formatter();
        out.format("===%n");
        for (int r = BOARD_SIZE - 1; r >= 0; r -= 1) {
            out.format("    ");
            for (int c = 0; c < BOARD_SIZE; c += 1) {
                out.format("%s ", get(sq(c, r)).abbrev());
            }
            out.format("%n");
        }
        out.format("Next move: %s%n===", turn().fullName());
        return out.toString();
    }

    /** Return true if a move from FROM to TO is blocked by an opposing
     *  piece or by a friendly piece on the target square. */
    private boolean blocked(Square from, Square to) {
        if (_board[to.index()] == _turn) {
            return true;
        }
        if (from.isValidMove(to)) {
            int dir = from.direction(to);
            for (int steps = 1; steps < from.distance(to); steps++) {
                Square sq = from.moveDest(dir, steps);
                if (sq != null) {
                    if (_board[sq.index()] == _turn.opposite()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** Return the size of the as-yet unvisited cluster of squares
     *  containing P at and adjacent to SQ.  VISITED indicates squares that
     *  have already been processed or are in different clusters.  Update
     *  VISITED to reflect squares counted. */
    private int numContig(Square sq, boolean[][] visited, Piece p) {
        int tot = 0;
        if (!visited[sq.row()][sq.col()]) {
            if (get(sq) == p) {
                tot++;
                visited[sq.row()][sq.col()] = true;
                for (Square sq1 : sq.adjacent()) {
                    tot = tot + numContig(sq1, visited, p);
                }
            }
        }

        return tot;
    }

    /** Set the values of _whiteRegionSizes and _blackRegionSizes. */
    private void computeRegions() {
        visits = new boolean[BOARD_SIZE][BOARD_SIZE];
        _whiteRegionSizes.clear();
        _blackRegionSizes.clear();
        _whiteRegionSizes.addAll(computeRegion(WP));
        _blackRegionSizes.addAll(computeRegion(BP));
        Collections.sort(_whiteRegionSizes, Collections.reverseOrder());
        Collections.sort(_blackRegionSizes, Collections.reverseOrder());
    }

    /** Return the sizes of all the regions in the current union-find
     *  structure for side S. */
    List<Integer> getRegionSizes(Piece s) {
        computeRegions();
        if (s == WP) {
            return _whiteRegionSizes;
        } else {
            return _blackRegionSizes;
        }
    }

    /** for board val.
     * @return list
     * @param p piece*/
    Collection<Integer> computeRegion(Piece p) {
        ArrayList<Integer> lst = new ArrayList<Integer>();
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                int sum = numContig(sq(c, r), visits, p);
                if (sum > 0) {
                    lst.add(sum);
                }
            }
        }
        return lst;
    }
    /** for board val.
     * @return num pieces
     * @param from square
     * @param to square*/
    int numPieces(Square from, Square to) {
        int tot = 0;
        if (from.isValidMove(to) && to.isValidMove(from)) {
            int dir = from.direction(to);
            int dir1 = to.direction(from);
            int steps = 1;
            Square sq = from.moveDest(dir, steps);
            while (sq != null) {
                if (_board[sq.index()] != EMP) {
                    tot++;
                }
                steps++;
                sq = from.moveDest(dir, steps);
            }
            steps = 1;
            sq = from.moveDest(dir1, steps);
            while (sq != null) {
                if (_board[sq.index()] != EMP) {
                    tot++;
                }
                steps++;
                sq = from.moveDest(dir1, steps);
            }

            return tot + 1;
        }
        return -11;
    }

    /** for board val.
     * @return integer */
    int calc() {
        int ownMoves = legalMoves().size();
        int oppMoves = legalMovesOpposite().size();
        return (ownMoves - 3 * oppMoves);
    }

    /** 2D Array showing the board's arrangement. */
    private static Piece[][] boardArray = new Piece[BOARD_SIZE][BOARD_SIZE];

    /** 2D Array of booleans. */
    private static boolean[][] visits = new boolean[BOARD_SIZE][BOARD_SIZE];

    /** 2D Array of booleans. */
    private static boolean[][] visited1 = new boolean[BOARD_SIZE][BOARD_SIZE];

    /** The standard initial configuration for Lines of Action (bottom row
     *  first). */
    static final Piece[][] INITIAL_PIECES = {
        { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP }
    };

    /** Current contents of the board.  Square S is at _board[S.index()]. */
    private final Piece[] _board = new Piece[BOARD_SIZE  * BOARD_SIZE];

    /** List of all unretracted moves on this board, in order. */
    private final ArrayList<Move> _moves = new ArrayList<>();
    /** Current side on move. */
    private Piece _turn;
    /** Limit on number of moves before tie is declared.  */
    private int _moveLimit;
    /** True iff the value of _winner is known to be valid. */
    private boolean _winnerKnown;
    /** Cached value of the winner (BP, WP, EMP (for tie), or null (game still
     *  in progress).  Use only if _winnerKnown. */
    private Piece _winner;

    /** True iff subsets computation is up-to-date. */
    private boolean _subsetsInitialized;


    /** List of the sizes of continguous clusters of pieces, by color. */
    private final ArrayList<Integer>
        _whiteRegionSizes = new ArrayList<>(),
        _blackRegionSizes = new ArrayList<>();
}
