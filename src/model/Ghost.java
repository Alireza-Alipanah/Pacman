package model;

import javafx.animation.TranslateTransition;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import view.Direction;

import java.util.ArrayList;

public class Ghost {

    private static final Image BLUE_GHOST;
    private static final Image RED_GHOST;
    private static final Image GREEN_GHOST;
    private static final Image ORANGE_GHOST;
    private static final Image GRAY_GHOST;
    private static final Image LIGHT_BLUE_GHOST;

    static {
        BLUE_GHOST = new Image("/resources/PNG/ghost_blue.png", 20, 20, true, false);
        RED_GHOST = new Image("/resources/PNG/ghost_red.png", 20, 20, true, false);
        GREEN_GHOST = new Image("/resources/PNG/ghost_green.png", 20, 20, true, false);
        ORANGE_GHOST = new Image("/resources/PNG/ghost_orange.png", 20, 20, true, false);
        GRAY_GHOST = new Image("/resources/PNG/ghost_gray.png", 20, 20, true, false);
        LIGHT_BLUE_GHOST = new Image("/resources/PNG/ghost_light_blue.png", 20, 20, true, false);
    }

    private final ArrayList<Direction> POSSIBLE_DIRECTIONS;
    private final int[] STATIC_GHOST_POSITION;
    private final int[] POSITION;
    private final GhostColor GHOST_COLOR;
    private final ImageView IMAGEVIEW;
    private final int[] POSSIBLE_OUTCOMES;
    private final Direction[] DIRECTIONS;
    private GhostColor currentColor;
    private int turnsRemainingAsFrightened;
    private Direction previousDirection;
    private TranslateTransition currentTransition;

    public Ghost(int i, int j, GhostColor GHOST_COLOR) {
        this.turnsRemainingAsFrightened = 0;
        this.GHOST_COLOR = currentColor = GHOST_COLOR;
        this.POSITION = new int[2];
        this.STATIC_GHOST_POSITION = new int[2];
        this.POSITION[0] = this.STATIC_GHOST_POSITION[0] = i;
        this.POSITION[1] = this.STATIC_GHOST_POSITION[1] = j;
        this.IMAGEVIEW = new ImageView();
        GridPane.setValignment(this.IMAGEVIEW, VPos.CENTER);
        GridPane.setHalignment(this.IMAGEVIEW, HPos.CENTER);
        this.previousDirection = Direction.NOTHING;
        this.POSSIBLE_DIRECTIONS = new ArrayList<>();
        this.POSSIBLE_OUTCOMES = new int[4];
        this.DIRECTIONS = new Direction[4];
        changeGhostColor(GHOST_COLOR);
    }

    public void changeGhostColor(GhostColor ghostColor) {
        switch (ghostColor) {
            case BLUE:
                IMAGEVIEW.setImage(BLUE_GHOST);
                break;
            case RED:
                IMAGEVIEW.setImage(RED_GHOST);
                break;
            case GREEN:
                IMAGEVIEW.setImage(GREEN_GHOST);
                break;
            case ORANGE:
                IMAGEVIEW.setImage(ORANGE_GHOST);
                break;
            case GRAY:
                IMAGEVIEW.setImage(GRAY_GHOST);
                break;
            case LIGHT_BLUE:
                IMAGEVIEW.setImage(LIGHT_BLUE_GHOST);
                break;
        }
        currentColor = ghostColor;
    }

    public int[] getPosition() {
        return POSITION;
    }

    public ImageView getImageView() {
        return IMAGEVIEW;
    }

    public GhostColor getGhostColor() {
        return GHOST_COLOR;
    }

    public void setTurnsRemainingAsFrightened(int turnsRemainingAsFrightened) {
        this.turnsRemainingAsFrightened = turnsRemainingAsFrightened;
    }

    public int getTurnsRemainingAsFrightened() {
        return turnsRemainingAsFrightened;
    }

    public void setPreviousDirection(Direction previousDirection) {
        this.previousDirection = previousDirection;
    }

    public Direction getPreviousDirection() {
        return previousDirection;
    }

    public ArrayList<Direction> getPossibleDirections() {
        return POSSIBLE_DIRECTIONS;
    }

    public void setCurrentColor(GhostColor currentColor) {
        this.currentColor = currentColor;
    }

    public GhostColor getCurrentColor() {
        return currentColor;
    }

    public void setCurrentTransition(TranslateTransition currentTransition) {
        this.currentTransition = currentTransition;
    }

    public TranslateTransition getCurrentTransition() {
        return currentTransition;
    }

    public int[] getPossibleOutcomes() {
        return POSSIBLE_OUTCOMES;
    }

    public Direction[] getDirections() {
        return DIRECTIONS;
    }

    public int[] getStaticGhostPosition() {
        return STATIC_GHOST_POSITION;
    }

    public void resetColor() {
        changeGhostColor(this.GHOST_COLOR);
    }
}
