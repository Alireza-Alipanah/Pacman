package model;

import javafx.scene.layout.GridPane;

public class Game {

    private final boolean HARD_DIFFICULTY;
    private final int[] PACMAN_POSITION;
    private final int[][] MAZE;
    private final int[] PACMAN_STATIC_POSITION;
    private final Ghost[] GHOSTS;
    private GridPane board;
    private boolean[][] visited;
    private boolean running;
    private int score;
    private int healthRemaining;
    private boolean ghostsArePaused;
    private int eatingGhostMultiplier;
    private int numberOfMapEntitiesEaten;
    private boolean playBeginning;

    public Game(GridPane board, int[][] MAZE, Ghost[] GHOSTS, boolean HARD_DIFFICULTY, int healthsRemaining) {
        this.board = board;
        this.visited = new boolean[51][51];
        this.running = false;
        this.PACMAN_POSITION = new int[2];
        this.PACMAN_POSITION[0] = this.PACMAN_POSITION[1] = 25;
        this.MAZE = MAZE;
        this.score = 0;
        this.GHOSTS = GHOSTS;
        this.HARD_DIFFICULTY = HARD_DIFFICULTY;
        this.healthRemaining = healthsRemaining;
        this.ghostsArePaused = true;
        this.eatingGhostMultiplier = 1;
        this.numberOfMapEntitiesEaten = 0;
        this.PACMAN_STATIC_POSITION = new int[2];
        this.PACMAN_STATIC_POSITION[0] = this.PACMAN_STATIC_POSITION[1] = 25;
        this.playBeginning = true;
    }

    public GridPane getBoard() {
        return board;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean getRunning() {
        return running;
    }

    public int[] getPacmanPosition() {
        return PACMAN_POSITION;
    }

    public int[][] getMaze() {
        return MAZE;
    }

    public boolean[][] getVisited() {
        return visited;
    }

    public int getScore() {
        return score;
    }

    public Ghost[] getGhosts() {
        return GHOSTS;
    }

    public boolean getHardDifficulty() {
        return HARD_DIFFICULTY;
    }

    public void setHealthRemaining(int healthRemaining) {
        this.healthRemaining = healthRemaining;
    }

    public int getHealthRemaining() {
        return healthRemaining;
    }

    public boolean getGhostsArePaused() {
        return ghostsArePaused;
    }

    public void setGhostsArePaused(boolean ghostsArePaused) {
        this.ghostsArePaused = ghostsArePaused;
    }

    public void setEatingGhostMultiplier(int eatingGhostMultiplier) {
        this.eatingGhostMultiplier = eatingGhostMultiplier;
    }

    public int getEatingGhostMultiplier() {
        return eatingGhostMultiplier;
    }

    public int getNumberOfMapEntitiesEaten() {
        return numberOfMapEntitiesEaten;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setBoard(GridPane board) {
        this.board = board;
    }

    public void setNumberOfMapEntitiesEaten(int numberOfMapEntitiesEaten) {
        this.numberOfMapEntitiesEaten = numberOfMapEntitiesEaten;
    }

    public void setVisited(boolean[][] visited) {
        this.visited = visited;
    }

    public int[] getPacmanStaticPosition() {
        return PACMAN_STATIC_POSITION;
    }

    public void setPlayBeginning(boolean playBeginning) {
        this.playBeginning = playBeginning;
    }

    public boolean getPlayBeginning() {
        return playBeginning;
    }

    public void addToScore(int amount) {
        score += amount;
    }

    public void addToNumberOfMapEntitiesEaten() {
        numberOfMapEntitiesEaten++;
    }
}
