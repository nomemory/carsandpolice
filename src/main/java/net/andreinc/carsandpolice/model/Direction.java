package net.andreinc.carsandpolice.model;

public enum Direction {

    NORTH(0, -1),
    SOUTH(0, 1),
    EAST(-1, 0),
    WEST(1, 0);

    private int x;
    private int y;

    public static Direction inverse(Direction direction) {
        switch (direction) {
            case SOUTH: return NORTH;
            case NORTH: return SOUTH;
            case EAST: return WEST;
            case WEST: return EAST;
        }
        throw new IllegalArgumentException();
    }

    Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
