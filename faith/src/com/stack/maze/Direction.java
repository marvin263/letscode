package com.stack.maze;

public enum Direction {
    EAST {
        public Direction next() {
            return SOUTH;
        }

        public Direction opposite() {
            return WEST;
        }
    },
    SOUTH {
        public Direction next() {
            return WEST;
        }

        public Direction opposite() {
            return NORTH;
        }
    },
    WEST {
        public Direction next() {
            return NORTH;
        }

        public Direction opposite() {
            return EAST;
        }
    },
    NORTH {
        public Direction next() {
            return EAST;
        }

        public Direction opposite() {
            return SOUTH;
        }
    };
    public abstract Direction next();

    public abstract Direction opposite();

    public static void main(String[] args) {

    }
}
