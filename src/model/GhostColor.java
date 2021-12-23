package model;

public enum GhostColor {
    BLUE("blue"),
    RED("red"),
    GREEN("green"),
    ORANGE("orange"),
    GRAY("gray"),
    LIGHT_BLUE("lightBlue");

    private final String COLOR_STRING;

    GhostColor(String COLOR_STRING) {
        this.COLOR_STRING = COLOR_STRING;
    }

    public static GhostColor getEnumByValue(String colorString) {
        switch (colorString) {
            case "red":
                return RED;
            case "green":
                return GREEN;
            case "orange":
                return ORANGE;
            case "gray":
                return GRAY;
            case "lightBlue":
                return LIGHT_BLUE;
            default:
                return BLUE;
        }
    }

    public String getValue() {
        return COLOR_STRING;
    }
}
