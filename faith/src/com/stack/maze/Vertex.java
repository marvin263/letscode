package com.stack.maze;

import java.util.HashSet;
import java.util.Set;

public class Vertex {
    public final MazeMap maze;
    public final int col;
    public final int row;
    /**
     * (never forever change) initial status of this Vertex
     */
    public final boolean blocked;

    private Set<Direction> setConsumed = new HashSet<Direction>(4);

    private boolean nonBlocked_but_has_been_discard = false;

    /**
     * 1. HAS BEEN tried
     * <p>
     * 2. this vertex tried this direction ACTIVELY
     * <p>
     * 3. When this vertex is pricked, we say this vertex passively consume a
     * direction ( being opposite of pricking vertex's direction)
     * 
     */
    private Direction justActivelyConsumed = null;

    public Vertex(final MazeMap maze, final int row, final int col,
            final boolean blocked) {
        this.maze = maze;
        this.col = col;
        this.row = row;
        this.blocked = blocked;
    }

    public void fillPassivelyConsumedDirection(Direction sourceGoDirec) {
        Direction passivelyConsumed = sourceGoDirec.opposite();
        setConsumed.add(passivelyConsumed);
        // none is actively consumed
        justActivelyConsumed = null;
    }

    private int[] calculateGoTo(int curRow, int curCol,
            Direction wantToGoDirec, int Row_MAX_INDEX, int Col_MAX_INDEX) {
        int row = -1;
        int col = -1;
        switch (wantToGoDirec) {
        case SOUTH:
            col = curCol;
            row = (curRow >= Row_MAX_INDEX) ? -1 : curRow + 1;
            break;
        case WEST:
            row = curRow;
            col = (curCol == 0) ? -1 : curCol - 1;
            break;
        case NORTH:
            col = curCol;
            row = (curRow == 0) ? -1 : curRow - 1;
            break;
        case EAST:
            row = curRow;
            col = (curCol == Col_MAX_INDEX) ? -1 : curCol + 1;
            break;
        }
        return new int[] { row, col };
    }

    /**
     * null-->tried all directions, finally failed
     * <p>
     * NOT null-->good, find the next vertex anyway
     * 
     * @return
     */
    public Vertex nextVertex() {
        // for each vertex, always start with EAST
        Direction nextGoToDirection = (justActivelyConsumed == null) ? Direction.EAST
                : justActivelyConsumed.next();
        System.out.print("Go into nextVertex(), current=" + this);
        while (setConsumed.size() < 4) {
            // direction already been tried
            if (setConsumed.contains(nextGoToDirection)) {
                // nextGoToDirection already been consumed
                // generate the new nextGoToDirection
                nextGoToDirection = nextGoToDirection.next();
            }
            // direction is available.
            // we can get the next vertex/(or null is border is reached) by this
            // direction
            else {
                int[] nextRowCol = calculateGoTo(row, col, nextGoToDirection,
                        maze.Row_MAX_INDEX, maze.Col_MAX_INDEX);
                // nextGoToDirection has been consumed
                setConsumed.add(nextGoToDirection);
                justActivelyConsumed = nextGoToDirection;
                // anyway, direction is ok.
                // but the reaching vertex may be null if border is reached
                Vertex nextVertex = maze.getV(nextRowCol[0], nextRowCol[1]);
                if (nextVertex == null) {
                    // nextGoToDirection already been consumed
                    // generate the new nextGoToDirection
                    nextGoToDirection = nextGoToDirection.next();
                }
                // best, find an existing vertex
                else {
                    System.out
                            .println("----Leave nextVertex(), good, find next, next="
                                    + nextVertex);
                    return nextVertex;
                }
            }
        }
        // For this vertex, we've tried all directions of it.
        // 4 directions have all been tried, but no available direction
        // Even direction is NOT available
        // discard this vertex
        nonBlocked_but_has_been_discard = true;
        System.out
                .println("----Leave nextVertex(), but NO next. Discard current="
                        + this);
        return null;
    }

    public boolean nonBlocked_but_has_been_discard() {
        return nonBlocked_but_has_been_discard;
    }

    public Direction getJustActivelyConsumed() {
        return justActivelyConsumed;
    }

    @Override
    public String toString() {
        return "Vertex [row=" + row + ", col=" + col + ", blocked=" + blocked
                + ", justActivelyConsumed=" + justActivelyConsumed + "]";
    }
}
