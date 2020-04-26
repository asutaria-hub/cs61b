/* Skeleton Copyright (C) 2015, 2020 Paul N. Hilfinger and the Regents of the
 * University of California.  All rights reserved. */
package loa;

import java.util.List;

import static loa.Piece.*;

/** An automated Player.
 *  @author Aayush Sutaria
 */
class MachinePlayer extends Player {

    /** A position-score magnitude indicating a win (for white if positive,
     *  black if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 20;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** A new MachinePlayer with no piece or controller (intended to produce
     *  a template). */
    MachinePlayer() {
        this(null, null);
    }

    /** A MachinePlayer that plays the SIDE pieces in GAME. */
    MachinePlayer(Piece side, Game game) {
        super(side, game);
    }

    @Override
    String getMove() {
        Move choice;

        assert side() == getGame().getBoard().turn();
        int depth;
        choice = searchForMove();
        getGame().reportMove(choice);
        return choice.toString();
    }

    @Override
    Player create(Piece piece, Game game) {
        return new MachinePlayer(piece, game);
    }

    @Override
    boolean isManual() {
        return false;
    }

    /** Return a move after searching the game tree to DEPTH>0 moves
     *  from the current position. Assumes the game is not over. */
    private Move searchForMove() {
        Board work = new Board(getBoard());
        int value;
        assert side() == work.turn();
        _foundMove = null;
        if (side() == WP) {
            value = findMove(work, chooseDepth(), -INFTY, INFTY, true);
        } else {
            value = findMove(work, chooseDepth(), -INFTY, INFTY, true);
        }
        return _foundMove;
    }

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _foundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _foundMove. If the game is over
     *  on BOARD, does not set _foundMove. */

    private int findMove(Board board, int depth, int alpha,
                         int beta, boolean saveMove) {
        if (depth == 2 || board.legalMoves().isEmpty() || board.gameOver()) {
            if (board.winner() == board.turn().opposite()) {
                return -INFTY;
            } else if (board.winner() == EMP) {
                return -INFTY;
            } else if (board.winner() == board.turn()) {
                return INFTY;
            } else {
                return board.calc();
            }
        }
        List<Move> legalMoves = board.legalMoves();
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        Board curr;
        if (board.turn() == WP) {
            for (int i = 0; i < legalMoves.size(); i++) {
                curr = new Board(board);
                Move mv = legalMoves.get(i);
                curr.makeMove(mv);
                int eval = findMove(curr, depth + 1, alpha, beta, false);
                max = Integer.max(eval, max);
                if (max == eval && saveMove) {
                    _foundMove = mv;
                }
                alpha = Integer.max(alpha, max);
                if (alpha >= beta) {
                    break;
                }
            }
            return max;
        } else {
            for (int i = 0; i < legalMoves.size(); i++) {
                curr = new Board(board);
                Move mv = legalMoves.get(i);
                curr.makeMove(mv);
                int eval = findMove(curr,
                        depth + 1, alpha, beta, false);
                min = Integer.min(eval, min);
                if (eval == min && saveMove) {
                    _foundMove = mv;
                }
                beta = Integer.min(beta, min);
                if (alpha >= beta) {
                    break;
                }
            }
            return min;
        }
    }


    /** Return a search depth for the current position. */
    private int chooseDepth() {
        return 1;
    }

    /** Used to convey moves discovered by findMove. */
    private Move _foundMove;








}
