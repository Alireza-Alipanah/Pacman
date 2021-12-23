package view;

import controller.GameController;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GameView implements Initializable {

    private static final GameController GAME_CONTROLLER;
    private static final Music MUSIC;

    static {
        GAME_CONTROLLER = GameController.getInstance();
        MUSIC = Music.getInstance();
    }

    private RotateTransition pacmanRotateTransition;
    private Timeline pacManMouthTimeLine;
    private Timeline pacManMovingTimeLine;
    private Timeline ghostMovingTimeLine;
    private Direction direction = Direction.RIGHT;
    private Arc pacMan;
    private Scene scene;
    private Timeline ghostStartingPauseTimeLine;
    private HBox info;
    private HBox healthBox;
    private Label scoreNumber;
    private Label healthRemaining;
    private boolean gameHasEnded;
    private TranslateTransition pacmanTranslateTransition;
    private boolean gameHasStarted;
    private TranslateTransition[] ghostTranslateTransition;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.gc();
        ghostTranslateTransition = new TranslateTransition[4];
        GAME_CONTROLLER.updateGhostTranslateTransitions(ghostTranslateTransition);
        gameHasStarted = false;
        gameHasEnded = false;
        pacMan = new Arc();
        makePacman(pacMan);
        makePacmanAnimation();
        GridPane.setHalignment(pacMan, HPos.CENTER);
        GridPane.setValignment(pacMan, VPos.CENTER);
        GAME_CONTROLLER.putPacMan(pacMan);
        makePacmanMovingProperties();
        makeGhostMovingProperties();
        GAME_CONTROLLER.putGhosts();
        makeHealthAndScoreBar();
        MUSIC.stopBackground();
        GAME_CONTROLLER.playBeginningSong();
    }

    private void makePacmanMovingProperties() {
        KeyFrame pacManMovingFrame = new KeyFrame(Duration.millis(143), move -> {
            GAME_CONTROLLER.tryToMovePacMan(direction, pacMan, pacmanTranslateTransition);
            pacmanTranslateTransition.play();
            collisionHandler();
            scoreNumber.setText(GAME_CONTROLLER.getScoreNumber());
            if (!gameHasEnded)
                if (GAME_CONTROLLER.pacManAteAllMapEntities()) {
                    pause();
                    GAME_CONTROLLER.resetGame(pacMan);
                    updateHealthBox();
                    resetPacman();
                    ((BorderPane) ((StackPane) scene.getRoot()).getChildren().get(0)).setCenter(GAME_CONTROLLER.getGameBoard());
                }
        });
        pacManMovingTimeLine = new Timeline();
        pacManMovingTimeLine.setCycleCount(Animation.INDEFINITE);
        pacManMovingTimeLine.getKeyFrames().add(pacManMovingFrame);
        pacmanTranslateTransition = new TranslateTransition();
        pacmanTranslateTransition.setNode(pacMan);
        pacmanTranslateTransition.setInterpolator(Interpolator.LINEAR);
        pacmanTranslateTransition.setDuration(GameController.getSmoothAnimation() ? Duration.millis(90) : Duration.ZERO);
        gameHasStarted = true;
    }

    private void collisionHandler() {
        if (GAME_CONTROLLER.collisionWithGhostChecker())
            if (GAME_CONTROLLER.handleCollisionWithGhost()) {
                MUSIC.playDeath();
                pause();
                if (!GAME_CONTROLLER.restartGame(pacMan)) {
                    try {
                        end();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    updateHealthBox();
                    resetPacman();
                }
            }
    }

    private void makeGhostMovingProperties() {
        KeyFrame ghostMovingFrame = new KeyFrame(Duration.millis(200), move -> {
            GAME_CONTROLLER.moveGhosts(ghostTranslateTransition);
            for (TranslateTransition translateTransition : ghostTranslateTransition) {
                translateTransition.play();
            }
            collisionHandler();
            scoreNumber.setText(GAME_CONTROLLER.getScoreNumber());
        });
        ghostMovingTimeLine = new Timeline();
        ghostMovingTimeLine.getKeyFrames().add(ghostMovingFrame);
        ghostMovingTimeLine.setCycleCount(Animation.INDEFINITE);
        ghostStartingPauseTimeLine = new Timeline();
        KeyFrame ghostStartingPauseFrame = new KeyFrame(Duration.seconds(2), finished -> GAME_CONTROLLER.unpauseGhosts());
        ghostStartingPauseTimeLine.getKeyFrames().add(ghostStartingPauseFrame);
    }

    private void makeHealthAndScoreBar() {
        info = new HBox();
        healthBox = new HBox();
        HBox scoreBox = new HBox();
        info.setSpacing(100);
        healthRemaining = new Label("healthRemaining : ");
        Label scoreLabel = new Label("score : ");
        scoreNumber = new Label("0");
        scoreBox.getChildren().addAll(scoreLabel, scoreNumber);
        updateHealthBox();
        info.getChildren().add(healthBox);
        info.getChildren().add(scoreBox);
    }

    private void makePacmanAnimation() {
        pacmanRotateTransition = new RotateTransition();
        pacmanRotateTransition.setFromAngle(0);
        pacmanRotateTransition.setToAngle(45);
        pacmanRotateTransition.setDuration(Duration.millis(143));
        pacmanRotateTransition.setAutoReverse(true);
        pacmanRotateTransition.setCycleCount(Animation.INDEFINITE);
        pacmanRotateTransition.setInterpolator(Interpolator.LINEAR);
        pacmanRotateTransition.setNode(pacMan);
        KeyValue start = new KeyValue(pacMan.lengthProperty(), 270);
        KeyValue end = new KeyValue(pacMan.lengthProperty(), 360);
        KeyFrame frame = new KeyFrame(Duration.millis(143), start, end);
        pacManMouthTimeLine = new Timeline(frame);
        pacManMouthTimeLine.setCycleCount(Animation.INDEFINITE);
        pacManMouthTimeLine.setAutoReverse(true);
    }

    private void makePacman(Arc pacMan) {
        pacMan.setCenterX(100);
        pacMan.setCenterY(100);
        pacMan.setType(ArcType.ROUND);
        pacMan.setRadiusX(8.5);
        pacMan.setRadiusY(8.5);
        pacMan.setFill(Color.rgb(198, 169, 29));
        pacMan.setStartAngle(45);
        pacMan.setLength(270);
    }

    private void updateHealthBox() {
        healthBox.getChildren().clear();
        healthBox.getChildren().add(healthRemaining);
        for (int numberOfLives = GAME_CONTROLLER.getNumberOfLives(); numberOfLives > 0; numberOfLives--) {
            Arc pacMan = new Arc();
            makePacman(pacMan);
            healthBox.getChildren().add(pacMan);
        }
    }

    public void initializeKeyPressAndInfoBar(Scene scene) {
        this.scene = scene;
        updateHealthBox();
        ((BorderPane) ((StackPane) scene.getRoot()).getChildren().get(0)).setTop(info);
        scene.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case DOWN:
                    direction = Direction.DOWN;
                    pacMan.setStartAngle(315);
                    if (GAME_CONTROLLER.isGamePaused())
                        resumeOrPause();
                    break;
                case UP:
                    direction = Direction.UP;
                    pacMan.setStartAngle(135);
                    if (GAME_CONTROLLER.isGamePaused())
                        resumeOrPause();
                    break;
                case LEFT:
                    direction = Direction.LEFT;
                    pacMan.setStartAngle(225);
                    if (GAME_CONTROLLER.isGamePaused())
                        resumeOrPause();
                    break;
                case RIGHT:
                    direction = Direction.RIGHT;
                    pacMan.setStartAngle(45);
                    if (GAME_CONTROLLER.isGamePaused())
                        resumeOrPause();
                    break;
                case SPACE:
                    resumeOrPause();
                    break;
                case ESCAPE:
                    try {
                        exit();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        });
    }

    @FXML
    public void resumeOrPause() {
        if (GAME_CONTROLLER.isGamePaused())
            resume();
        else pause();
    }

    private void resume() {
        if (gameHasStarted) {
            pacmanRotateTransition.play();
            pacManMouthTimeLine.play();
            GAME_CONTROLLER.resume();
            ghostMovingTimeLine.play();
            ghostStartingPauseTimeLine.play();
            pacManMovingTimeLine.play();
        }
    }

    private void pause() {
        pacmanRotateTransition.stop();
        pacManMouthTimeLine.stop();
        ghostMovingTimeLine.stop();
        GAME_CONTROLLER.pause();
        pacManMovingTimeLine.stop();
        switch (direction) {
            case DOWN:
                pacMan.setStartAngle(315);
                break;
            case UP:
                pacMan.setStartAngle(135);
                break;
            case LEFT:
                pacMan.setStartAngle(225);
                break;
            case RIGHT:
                pacMan.setStartAngle(45);
                break;
        }
        pacMan.setLength(270);
    }

    @FXML
    public void exit() throws IOException {
        pause();
        GAME_CONTROLLER.removePacMan(pacMan);
        GAME_CONTROLLER.removeGhosts();
        resetPacman();
        GAME_CONTROLLER.updateStaticPosition();
        goToMainMenu();
    }

    private void end() throws IOException {
        pause();
        gameHasEnded = true;
        GAME_CONTROLLER.addScoreToUser();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("game over");
        alert.setHeaderText(GameController.getInstance().getEndResults());
        alert.show();
        goToMainMenu();
    }

    private void goToMainMenu() throws IOException {
        Stage stage = (Stage) scene.getWindow();
        Parent pane = FXMLLoader.load(getClass().getResource("/resources/Main.fxml"));
        Scene scene = new Scene(pane);
        stage.setScene(scene);
    }

    private void resetPacman() {
        pacmanTranslateTransition.stop();
        TranslateTransition translateTransition = new TranslateTransition();
        translateTransition.setNode(pacMan);
        translateTransition.setDuration(Duration.millis(1));
        translateTransition.setByX(pacMan.getTranslateX() * -1);
        translateTransition.setByY(pacMan.getTranslateY() * -1);
        translateTransition.play();
    }
}
