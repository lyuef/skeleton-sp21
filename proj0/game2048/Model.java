package game2048;

import java.util.Formatter;
import java.util.Observable;


/** The state of a game of 2048.
 *  @author TODO: YOUR NAME HERE
 */
public class Model extends Observable {
    /** Current contents of the board. */
    private Board board;
    /** Current score. */
    private int score;
    /** Maximum score so far.  Updated when game ends. */
    private int maxScore;
    /** True iff game is ended. */
    private boolean gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
        gameOver = false;
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        int size = rawValues.length;
        board = new Board(rawValues, score);
        this.score = score;
        this.maxScore = maxScore;
        this.gameOver = gameOver;
    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there.
     *  Used for testing. Should be deprecated and removed.
     *  */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /** Return the number of squares on one side of the board.
     *  Used for testing. Should be deprecated and removed. */
    public int size() {
        return board.size();
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        checkGameOver();
        if (gameOver) {
            maxScore = Math.max(score, maxScore);
        }
        return gameOver;
    }

    /** Return the current score. */
    public int score() {
        return score;
    }

    /** Return the current maximum game score (updated at end of game). */
    public int maxScore() {
        return maxScore;
    }

    /** Clear the board to empty and reset the score. */
    public void clear() {
        score = 0;
        gameOver = false;
        board.clear();
        setChanged();
    }

    /** Add TILE to the board. There must be no Tile currently at the
     *  same position. */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /** Tilt the board toward SIDE. Return true iff this changes the board.
     *
     * 1. If two Tile objects are adjacent in the direction of motion and have
     *    the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     * */
    private boolean change_col_up(int c) {
        int lst = this.board.size();
        boolean changed = false;
        for(int r=this.board.size()-1;r>-1;r--) {
            if(this.board.tile(c,r) == null) continue;
            if(lst > -1 && lst < this.board.size() && this.board.tile(c,lst)!=null &&this.board.tile(c,lst).value() == this.board.tile(c,r).value()) {
                this.score += 2*this.board.tile(c,r).value();
                this.board.move(c,lst,this.board.tile(c,r));
                lst -- ;
                changed = true;
            } else if(lst>-1 && lst < this.board.size() && this.board.tile(c,lst) == null){
                this.board.move(c,lst,this.board.tile(c,r));
                changed = true;
            } else {
                changed |= (lst-1>r);
                this.board.move(c,lst-1,this.board.tile(c,r));
                lst -- ;
            }
        }
        return changed ;
    }
    private boolean change_col_down(int c) {
        int lst = -1;
        boolean changed = false;
        for(int r=0;r<this.board.size();r++) {
            if(this.board.tile(c,r) == null) continue;
            if(lst > -1 && lst < this.board.size() && this.board.tile(c,lst)!=null &&this.board.tile(c,lst).value() == this.board.tile(c,r).value()) {
                this.score += 2*this.board.tile(c,r).value();
                this.board.move(c,lst,this.board.tile(c,r));
                lst ++ ;
                changed = true;
            } else if(lst>-1 && lst < this.board.size() && this.board.tile(c,lst) == null){
                this.board.move(c,lst,this.board.tile(c,r));
                changed = true;
            } else {
                changed |= (lst+1<r);
                this.board.move(c,lst+1,this.board.tile(c,r));
                lst ++ ;
            }
        }
        return changed ;
    }
    private boolean change_row_left(int r) {
        int lst = -1;
        boolean changed = false;
        for(int c=0;c<this.board.size();c++) {
            if(this.board.tile(c,r) == null) continue;
            if(lst > -1 && lst < this.board.size() && this.board.tile(lst,r)!=null &&this.board.tile(lst,r).value() == this.board.tile(c,r).value()) {
                this.score += 2*this.board.tile(c,r).value();
                this.board.move(lst,r,this.board.tile(c,r));
                lst ++ ;
                changed = true;
            } else if(lst>-1 && lst < this.board.size() && this.board.tile(lst,r) == null){
                this.board.move(lst,r,this.board.tile(c,r));
                changed = true;
            } else {
                changed |= (lst + 1 < c );
                this.board.move(lst+1,r,this.board.tile(c,r));
                lst ++ ;
            }
        }
        return changed ;
    }
    private boolean change_row_right(int r) {
        int lst = this.board.size();
        boolean changed = false;
        for(int c=this.board.size()-1;c>-1;c--) {
            if(this.board.tile(c,r) == null) continue;
            if(lst > -1 && lst <this.board.size() && this.board.tile(lst,r)!=null &&this.board.tile(lst,r).value() == this.board.tile(c,r).value()) {
                this.score += 2*this.board.tile(c,r).value();
                this.board.move(lst,r,this.board.tile(c,r));
                lst -- ;
                changed = true;
            } else if(lst>-1 && lst < this.board.size() && this.board.tile(lst,r) == null){
                this.board.move(lst,r,this.board.tile(c,r));
                changed = true;
            } else {
                changed |= (lst - 1 > c );
                this.board.move(lst-1,r,this.board.tile(c,r));
                lst -- ;
            }
        }
        return changed ;
    }
    public boolean tilt(Side side) {
        boolean changed;
        changed = false;

        // TODO: Modify this.board (and perhaps this.score) to account
        // for the tilt to the Side SIDE. If the board changed, set the
        // changed local variable to true.
        if(side == Side.NORTH) {
            for(int i=0;i<this.board.size();i++) {
                changed |= change_col_up(i);
            }
        }
        if(side == Side.SOUTH) {
            for(int i=0;i<this.board.size();i++) {
                changed |= change_col_down(i);
            }
        }
        if(side == Side.WEST) {
            for(int i=0;i<this.board.size();i++) {
                changed |=change_row_left(i);
            }
        }
        if(side == Side.EAST) {
            for(int i=0;i<this.board.size();i++) {
                changed |= change_row_right(i);
            }
        }
        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }

    /** Checks if the game is over and sets the gameOver variable
     *  appropriately.
     */
    private void checkGameOver() {
        gameOver = checkGameOver(board);
    }

    /** Determine whether game is over. */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /** Returns true if at least one space on the Board is empty.
     *  Empty spaces are stored as null.
     * */
    public static boolean emptySpaceExists(Board b) {
        // TODO: Fill in this function.
        for(int i=0;i<b.size();i++) {
            for(int j=0;j<b.size();j++) {
                if(b.tile(i,j)==null) return true;
            }
        }
        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {
        // TODO: Fill in this function.
        for(int i=0;i<b.size();i+=1) {
            for(int j=0;j<b.size();j+=1) {
                if(b.tile(i,j) == null) continue;
                if( b.tile(i,j).value() == MAX_PIECE )
                    return true;
            }
        }
        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public static boolean atLeastOneMoveExists(Board b) {
        // TODO: Fill in this function.
        int [][] d = {
                {-1,0},
                {1,0},
                {0,1},
                {0,-1}
        };
        for(int i=0;i<b.size();i++) {
            for(int j=0;j<b.size();j++) {
                if(b.tile(i,j)==null) return true;
                else for(int k=0;k<4;k++) {
                    if(i+d[k][0]>=0 && i+d[k][0]<b.size() && j+d[k][1]>=0 && j+d[k][1]<b.size() && b.tile(i+d[k][0],j+d[k][1])!=null) {
                        if(b.tile(i,j).value() == b.tile(i+d[k][0],j+d[k][1]).value()) return true;
                    }
                }
            }
        }
        for(int i=0;i<b.size();i++) {
            for(int j=0;j<b.size();j++) {

            }
        }
        return false;
    }


    @Override
     /** Returns the model as a string, used for debugging. */
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    /** Returns whether two models are equal. */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    /** Returns hash code of Model’s string. */
    public int hashCode() {
        return toString().hashCode();
    }
}
