package controller;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import model.Game;
import model.Ghost;
import model.GhostColor;
import model.User;
import view.Direction;
import view.Music;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class GameController {

    private static final Random RANDOM;
    private static final Music MUSIC;
    private static GameController gameController;
    public static boolean smoothAnimation;

    private Direction[] directions;

    static {
        RANDOM = new Random();
        MUSIC = Music.getInstance();
        smoothAnimation = true;
    }

    private GameController() {
        directions = new Direction[2];
    }

    public static GameController getInstance() {
        if (gameController == null)
            gameController = new GameController();
        return gameController;
    }

    public static void setSmoothAnimation(boolean smoothAnimation) {
        GameController.smoothAnimation = smoothAnimation;
    }

    public static boolean getSmoothAnimation() {
        return smoothAnimation;
    }

    public void startANewGame(GridPane board, int[][] maze, boolean hardDifficulty, int numberOfHealths) {
        User user = User.getLoggedIn();
        Game game = new Game(board, maze, makeGhosts(), hardDifficulty, numberOfHealths);
        boolean[][] visited = game.getVisited();
        visited[25][25] = true;
        visited[1][1] = true;
        visited[1][49] = true;
        visited[49][1] = true;
        visited[49][49] = true;
        user.setCurrentGame(game);
    }

    private Ghost[] makeGhosts() {
        Ghost[] ghosts = new Ghost[4];
        ghosts[0] = new Ghost(1, 1, GhostColor.RED);
        ghosts[1] = new Ghost(1, 49, GhostColor.GREEN);
        ghosts[2] = new Ghost(49, 1, GhostColor.ORANGE);
        ghosts[3] = new Ghost(49, 49, GhostColor.GRAY);
        for (Ghost ghost : ghosts) {
            GridPane.setHalignment(ghost.getImageView(), HPos.CENTER);
            GridPane.setValignment(ghost.getImageView(), VPos.CENTER);
        }
        return ghosts;
    }

    public void putGhosts() {
        Game game = User.getLoggedIn().getCurrentGame();
        Ghost[] ghosts = game.getGhosts();
        GridPane board = game.getBoard();
        int[] staticGhostPosition;
        for (Ghost ghost : ghosts) {
            staticGhostPosition = smoothAnimation ? ghost.getStaticGhostPosition() : ghost.getPosition();
            board.add(ghost.getImageView(), staticGhostPosition[0], staticGhostPosition[1]);
        }
    }

    public void putPacMan(Arc pacman) {
        Game game = User.getLoggedIn().getCurrentGame();
        GridPane board = game.getBoard();
        int[] pacmanStaticPosition = smoothAnimation ? game.getPacmanStaticPosition() : game.getPacmanPosition();
        board.add(pacman, pacmanStaticPosition[0], pacmanStaticPosition[1]);
    }

    public void updateStaticPosition() {
        Game game = User.getLoggedIn().getCurrentGame();
        int[] pacmanPosition = game.getPacmanPosition();
        int[] pacmanStaticPosition = game.getPacmanStaticPosition();
        pacmanStaticPosition[0] = pacmanPosition[0];
        pacmanStaticPosition[1] = pacmanPosition[1];
    }

    public void resume() {
        Game game = User.getLoggedIn().getCurrentGame();
        game.setRunning(true);
    }

    public void pause() {
        Game game = User.getLoggedIn().getCurrentGame();
        game.setRunning(false);
    }

    public void removePacMan(Arc pacman) {
        Game game = User.getLoggedIn().getCurrentGame();
        GridPane board = game.getBoard();
        board.getChildren().remove(pacman);
    }

    public void tryToMovePacMan(Direction direction, Arc pacman, TranslateTransition translateTransition) {
        Game game = User.getLoggedIn().getCurrentGame();
        GridPane board = game.getBoard();
        int[] position = game.getPacmanPosition();
        int[][] maze = game.getMaze();
        movePacman(direction, translateTransition, position, maze);
        board.getChildren().remove(pacman);
        collisionWithMapEntities(game, board, game.getVisited(), position, maze);
        putPacMan(pacman);
    }

    private void movePacman(Direction direction, TranslateTransition translateTransition, int[] position, int[][] maze) {
        switch (direction) {
            case RIGHT:
                translateTransition.setByY(0);
                if (maze[position[1]][position[0] + 1] != 1) {
                    translateTransition.setByX(24);
                    position[0] += 2;
                } else translateTransition.setByX(0);
                break;
            case LEFT:
                translateTransition.setByY(0);
                if (maze[position[1]][position[0] - 1] != 1) {
                    translateTransition.setByX(-24);
                    position[0] -= 2;
                } else translateTransition.setByX(0);
                break;
            case UP:
                translateTransition.setByX(0);
                if (maze[position[1] - 1][position[0]] != 1) {
                    translateTransition.setByY(-24);
                    position[1] -= 2;
                } else translateTransition.setByY(0);
                break;
            case DOWN:
                translateTransition.setByX(0);
                if (maze[position[1] + 1][position[0]] != 1) {
                    translateTransition.setByY(24);
                    position[1] += 2;
                } else translateTransition.setByY(0);
                break;
        }
    }

    private void collisionWithMapEntities(Game game, GridPane board, boolean[][] visited, int[] position, int[][] maze) {
        if (!visited[position[1]][position[0]]) {
            if (maze[position[1]][position[0]] == 2) {
                game.addToScore(5);
            } else if (maze[position[1]][position[0]] == 4) {
                MUSIC.playIntermission();
                Ghost[] ghosts = game.getGhosts();
                for (Ghost ghost : ghosts) {
                    ghost.changeGhostColor(GhostColor.BLUE);
                    ghost.setTurnsRemainingAsFrightened(50);
                }
                game.setEatingGhostMultiplier(1);
            }
            Rectangle rectangle = new Rectangle(1, 1, 20, 20);
            rectangle.setFill(Color.rgb(16, 53, 104));
            board.add(rectangle, position[0], position[1]);
            visited[position[1]][position[0]] = true;
            game.addToNumberOfMapEntitiesEaten();
        }
    }

    public boolean isGamePaused() {
        return !User.getLoggedIn().getCurrentGame().getRunning();
    }

    public void moveGhosts(TranslateTransition[] translateTransitions) {
        Game game = User.getLoggedIn().getCurrentGame();
        Ghost[] ghosts = game.getGhosts();
        if (!game.getGhostsArePaused()) {
            int[][] maze = game.getMaze();
            int[] pacMamPosition = game.getPacmanPosition();
            GridPane board = game.getBoard();
            int[] position;
            boolean hardDifficulty = game.getHardDifficulty();
            for (Ghost ghost : ghosts) {
                if (ghost.getCurrentColor() != GhostColor.LIGHT_BLUE) {
                    position = ghost.getPosition();
                    if (hardDifficulty)
                        TryToMoveGhostTowardPacMan(ghost, position, maze, pacMamPosition, translateTransitions);
                    else changeGhostPositionRandomly(ghost, position, maze, translateTransitions);
                } else translateGhost(ghost, Direction.NOTHING, translateTransitions);
                board.getChildren().remove(ghost.getImageView());
                if (ghost.getTurnsRemainingAsFrightened() > 0) {
                    ghost.setTurnsRemainingAsFrightened(ghost.getTurnsRemainingAsFrightened() - 1);
                    if (ghost.getTurnsRemainingAsFrightened() == 0) {
                        resetGhost(ghost, ghost.getCurrentColor() == GhostColor.LIGHT_BLUE);
                        ghost.resetColor();
                    }
                }
            }
            putGhosts();
        } else {
            for (Ghost ghost : ghosts) {
                translateGhost(ghost, Direction.NOTHING, translateTransitions);
            }
        }
    }

    private void changeGhostPositionRandomly(Ghost ghost, int[] position, int[][] maze,
                                             TranslateTransition[] translateTransitions) {
        ArrayList<Direction> possibleDirections = getPossibleMoves(ghost, position, maze);
        removeUnwantedDirections(ghost, possibleDirections);
        Direction direction;
        direction = possibleDirections.get(RANDOM.nextInt(possibleDirections.size()));
        moveGhost(ghost, position, translateTransitions, direction);
    }

    private void moveGhost(Ghost ghost, int[] position, TranslateTransition[] translateTransitions, Direction direction) {
        switch (direction) {
            case RIGHT:
                position[0] += 2;
                break;
            case LEFT:
                position[0] -= 2;
                break;
            case UP:
                position[1] -= 2;
                break;
            case DOWN:
                position[1] += 2;
                break;
        }
        translateGhost(ghost, direction, translateTransitions);
        ghost.setPreviousDirection(direction);
    }

    private void removeUnwantedDirections(Ghost ghost, ArrayList<Direction> possibleDirections) {
        Direction direction = ghost.getPreviousDirection();
        if (possibleDirections.size() > 1 && direction != Direction.NOTHING)
            if (direction == Direction.RIGHT)
                possibleDirections.remove(Direction.LEFT);
            else if (direction == Direction.LEFT)
                possibleDirections.remove(Direction.RIGHT);
            else if (direction == Direction.UP)
                possibleDirections.remove(Direction.DOWN);
            else possibleDirections.remove(Direction.UP);
    }

    private ArrayList<Direction> getPossibleMoves(Ghost ghost, int[] position, int[][] maze) {
        ArrayList<Direction> possibleDirections = ghost.getPossibleDirections();
        possibleDirections.clear();
        if (maze[position[1]][position[0] + 1] != 1)
            possibleDirections.add(Direction.RIGHT);
        if (maze[position[1]][position[0] - 1] != 1)
            possibleDirections.add(Direction.LEFT);
        if (maze[position[1] - 1][position[0]] != 1)
            possibleDirections.add(Direction.UP);
        if (maze[position[1] + 1][position[0]] != 1)
            possibleDirections.add(Direction.DOWN);
        return possibleDirections;
    }

    private void TryToMoveGhostTowardPacMan(Ghost ghost, int[] position, int[][] maze,
                                            int[] pacmanPosition, TranslateTransition[] translateTransitions) {
        ArrayList<Direction> possibleDirections = getPossibleMoves(ghost, position, maze);
        if (possibleDirections.size() > 1) {
            removeUnwantedDirections(ghost, possibleDirections);
            sortDirectionsBasedOnPriority(possibleDirections, pacmanPosition, position);
        }
        moveGhost(ghost, position, translateTransitions,
                possibleDirections.size() > 1 ?
                        (ghost.getCurrentColor() != GhostColor.BLUE ? directions[0] : directions[1])
                        : possibleDirections.get(0));
    }

    private void sortDirectionsBasedOnPriority(ArrayList<Direction> possibleDirections, int[] pacmanPosition, int[] position) {
        int distance;
        directions[0] = directions[1] = Direction.NOTHING;
        for (int i = 0; i < possibleDirections.size(); i++) {
            if (possibleDirections.get(i) == Direction.RIGHT)
                distance = pacmanPosition[0] - position[0];
            else if (possibleDirections.get(i) == Direction.LEFT)
                distance = position[0] - pacmanPosition[0];
            else if (possibleDirections.get(i) == Direction.UP)
                distance = position[1] - pacmanPosition[1];
            else distance = pacmanPosition[1] - position[1];
            if (distance > 0)
                directions[0] = directions[0] == Direction.NOTHING || RANDOM.nextBoolean() ?
                        possibleDirections.get(i) : directions[0];
            else directions[1] = directions[1] == Direction.NOTHING || RANDOM.nextBoolean() ?
                        possibleDirections.get(i) : directions[1];
        }
        if (directions[0] == Direction.NOTHING)
            directions[0] = directions[1];
        if (directions[1] == Direction.NOTHING)
            directions[1] = directions[0];
    }

    public boolean collisionWithGhostChecker() {
        Game game = User.getLoggedIn().getCurrentGame();
        int[] pacmanPosition = game.getPacmanPosition();
        Ghost[] ghosts = game.getGhosts();
        int[] ghostPosition;
        for (Ghost ghost : ghosts) {
            ghostPosition = ghost.getPosition();
            if (ghostPosition[0] == pacmanPosition[0] && ghostPosition[1] == pacmanPosition[1])
                return true;
        }
        return false;
    }

    public boolean handleCollisionWithGhost() {
        Game game = User.getLoggedIn().getCurrentGame();
        int[] pacmanPosition = game.getPacmanPosition();
        Ghost[] ghosts = game.getGhosts();
        int[] ghostPosition;
        for (Ghost ghost : ghosts) {
            ghostPosition = ghost.getPosition();
            if (ghostPosition[0] == pacmanPosition[0] && ghostPosition[1] == pacmanPosition[1]) {
                if (ghost.getCurrentColor() != GhostColor.BLUE && ghost.getCurrentColor() != GhostColor.LIGHT_BLUE)
                    return true;
                else if (ghost.getCurrentColor() == GhostColor.BLUE) {
                    MUSIC.playEatGhost();
                    game.addToScore(game.getEatingGhostMultiplier() * 200);
                    game.setEatingGhostMultiplier(game.getEatingGhostMultiplier() + 1);
                    ghost.changeGhostColor(GhostColor.LIGHT_BLUE);
                    resetTranslate(ghost.getImageView());
                    ghost.setTurnsRemainingAsFrightened(25);
                    moveGhostBackToStartingPosition(game.getBoard(), ghost);
                }
            }
        }
        return false;
    }

    private void moveGhostBackToStartingPosition(GridPane board, Ghost ghost) {
        board.getChildren().remove(ghost.getImageView());
        int[] ghostPosition = ghost.getPosition();
        int[] staticGhostPosition = ghost.getStaticGhostPosition();
        if (ghost.getCurrentTransition() != null)
            ghost.getCurrentTransition().stop();
        mergePositions(ghost, ghostPosition, staticGhostPosition);
        board.getChildren().add(ghost.getImageView());
    }

    private void mergePositions(Ghost ghost, int[] ghostPosition, int[] staticGhostPosition) {
        staticGhostPosition[0] = ghostPosition[0] = ghost.getGhostColor() == GhostColor.RED ||
                ghost.getGhostColor() == GhostColor.GREEN ? 1 : 49;
        staticGhostPosition[1] = ghostPosition[1] = ghost.getGhostColor() == GhostColor.RED ||
                ghost.getGhostColor() == GhostColor.ORANGE ? 1 : 49;
    }

    public boolean restartGame(Arc pacman) {
        Game game = User.getLoggedIn().getCurrentGame();
        int[] pacmanPosition = game.getPacmanPosition();
        Ghost[] ghosts = game.getGhosts();
        for (Ghost ghost : ghosts) {
            if (ghost.getCurrentTransition() != null)
                ghost.getCurrentTransition().stop();
            ghost.resetColor();
            resetTranslate(ghost.getImageView());
            resetGhost(ghost, true);
        }
        pacmanPosition[0] = pacmanPosition[1] = 25;
        removePacMan(pacman);
        int[] staticPacmanPosition = game.getPacmanStaticPosition();
        staticPacmanPosition[0] = staticPacmanPosition[1] = 25;
        putPacMan(pacman);
        removeGhosts();
        putGhosts();
        game.setHealthRemaining(game.getHealthRemaining() - 1);
        pauseGhosts();
        return game.getHealthRemaining() > 0;
    }

    private void resetGhost(Ghost ghost, boolean mergePositions) {
        int[] ghostPosition = ghost.getPosition();
        int[] staticGhostPosition = ghost.getStaticGhostPosition();
        if (mergePositions)
            mergePositions(ghost, ghostPosition, staticGhostPosition);
        ghost.setTurnsRemainingAsFrightened(0);
        ghost.setPreviousDirection(Direction.NOTHING);
    }

    public String getEndResults() {
        Game game = User.getLoggedIn().getCurrentGame();
        User.getLoggedIn().setCurrentGame(null);
        return "Game Over!\nyou managed to get " + game.getScore() + " score(s) from this game";
    }

    public void removeGhosts() {
        Game game = User.getLoggedIn().getCurrentGame();
        Ghost[] ghosts = game.getGhosts();
        GridPane board = game.getBoard();
        for (Ghost ghost : ghosts) {
            board.getChildren().remove(ghost.getImageView());
        }
    }

    public void unpauseGhosts() {
        if (User.getLoggedIn().getCurrentGame() != null)
            User.getLoggedIn().getCurrentGame().setGhostsArePaused(false);
    }

    public void pauseGhosts() {
        if (User.getLoggedIn().getCurrentGame() != null)
            User.getLoggedIn().getCurrentGame().setGhostsArePaused(true);
    }

    public int getNumberOfLives() {
        if (User.getLoggedIn().getCurrentGame() != null)
            return User.getLoggedIn().getCurrentGame().getHealthRemaining();
        else return 0;
    }

    public String getScoreNumber() {
        if (User.getLoggedIn().getCurrentGame() != null)
            return Integer.toString(User.getLoggedIn().getCurrentGame().getScore());
        else return null;
    }

    public void addScoreToUser() {
        User user = User.getLoggedIn();
        int userScore = user.getScore();
        int gameScore = user.getCurrentGame().getScore();
        if (userScore < gameScore) {
            user.setScore(gameScore);
            user.setLastScoreTime(LocalDateTime.now());
        }
    }

    public boolean pacManAteAllMapEntities() {
        return User.getLoggedIn().getCurrentGame().getNumberOfMapEntitiesEaten() == 620;
    }

    public void resetGame(Arc pacman) {
        MUSIC.playExtrapac();
        Game game = User.getLoggedIn().getCurrentGame();
        game.setNumberOfMapEntitiesEaten(0);
        game.setHealthRemaining(game.getHealthRemaining() + 2);
        boolean[][] visited = game.getVisited();
        for (boolean[] visitedRow : visited) {
            Arrays.fill(visitedRow, false);
        }
        game.setBoard(MazeController.getInstance().makeMazeGrid(game.getMaze()));
        restartGame(pacman);
    }

    public GridPane getGameBoard() {
        return User.getLoggedIn().getCurrentGame().getBoard();
    }

    private void translateGhost(Ghost ghost, Direction direction, TranslateTransition[] translateTransitions) {
        int i;
        switch (ghost.getGhostColor()) {
            case RED:
                i = 0;
                break;
            case GREEN:
                i = 1;
                break;
            case ORANGE:
                i = 2;
                break;
            default:
                i = 3;
                break;
        }
        switch (direction) {
            case RIGHT:
                translateTransitions[i].setByX(24);
                translateTransitions[i].setByY(0);
                break;
            case LEFT:
                translateTransitions[i].setByX(-24);
                translateTransitions[i].setByY(0);
                break;
            case UP:
                translateTransitions[i].setByX(0);
                translateTransitions[i].setByY(-24);
                break;
            case DOWN:
                translateTransitions[i].setByX(0);
                translateTransitions[i].setByY(24);
                break;
            default:
                translateTransitions[i].setByX(0);
                translateTransitions[i].setByY(0);
                break;
        }
        ghost.setCurrentTransition(translateTransitions[i]);
    }

    private void resetTranslate(ImageView imageView) {
        TranslateTransition translateTransition = new TranslateTransition();
        translateTransition.setDuration(Duration.millis(1));
        translateTransition.setByX(-1 * imageView.getTranslateX());
        translateTransition.setByY(-1 * imageView.getTranslateY());
        translateTransition.setNode(imageView);
        translateTransition.play();
    }

    public void playBeginningSong() {
        Game game = User.getLoggedIn().getCurrentGame();
        if (game != null)
            if (game.getPlayBeginning()) {
                MUSIC.playBeginning();
                game.setPlayBeginning(false);
            }
    }

    public void updateGhostTranslateTransitions(TranslateTransition[] translateTransitions) {
        Ghost[] ghosts = User.getLoggedIn().getCurrentGame().getGhosts();
        for (int i = 0; i < ghosts.length; i++) {
            translateTransitions[i] = new TranslateTransition();
            translateTransitions[i].setInterpolator(Interpolator.LINEAR);
            translateTransitions[i].setDuration(smoothAnimation ? Duration.millis(147) : Duration.ZERO);
            translateTransitions[i].setNode(ghosts[i].getImageView());
        }
    }

    public void adjustToNewSettings() {
        Game game = User.getLoggedIn().getCurrentGame();
        if (game != null) {
            Ghost[] ghosts = game.getGhosts();
            int[] position, staticPosition;
            for (Ghost ghost : ghosts) {
                position = ghost.getPosition();
                staticPosition = ghost.getStaticGhostPosition();
                if (smoothAnimation) {
                    staticPosition[0] = position[0];
                    staticPosition[1] = position[1];
                } else {
                    resetTranslate(ghost.getImageView());
                }
            }
        }
    }
}
