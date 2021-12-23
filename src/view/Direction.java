package view;

public enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT,
    NOTHING;

    public static Direction getDirectionByString(String direction) {
        switch (direction) {
            case "RIGHT":
                return RIGHT;
            case "LEFT":
                return LEFT;
            case "UP":
                return UP;
            case "DOWN":
                return DOWN;
            default:
                return NOTHING;
        }
    }
}
