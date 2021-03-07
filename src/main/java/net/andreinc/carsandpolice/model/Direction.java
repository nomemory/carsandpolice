package net.andreinc.carsandpolice.model;

import lombok.Getter;

public enum Direction {

    NORTH(0, -1),
    SOUTH(0, 1),
    EAST(-1, 0),
    WEST(1, 0);

    @Getter
    private int x;
    @Getter
    private int y;

    public static Direction inverse(Direction direction) {
        switch (direction) {
            case SOUTH: return NORTH;
            case NORTH: return SOUTH;
            case EAST: return WEST;
            case WEST: return EAST;
        }
        throw new IllegalStateException();
    }
    Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
