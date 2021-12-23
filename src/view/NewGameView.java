package view;

import controller.GameController;
import controller.MazeController;
import controller.UserController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NewGameView implements Initializable {

    private static final UserController USER_CONTROLLER;
    private static final MazeController MAZE_CONTROLLER;
    private static final GameController GAME_CONTROLLER;

    static {
        USER_CONTROLLER = UserController.getInstance();
        MAZE_CONTROLLER = MazeController.getInstance();
        GAME_CONTROLLER = GameController.getInstance();
    }

    @FXML
    private BorderPane border;
    @FXML
    private Spinner<Integer> healthSpinner;

    private int[][] selectedMap;
    private GridPane selectedMapPane;
    private boolean hardDifficulty;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showNextMap();
        hardDifficulty = false;
        SpinnerValueFactory<Integer> spinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 5, 2);
        healthSpinner.setValueFactory(spinnerValueFactory);
    }

    @FXML
    private void showNextMap() {
        selectedMap = USER_CONTROLLER.getNextUserMap(selectedMap);
        selectedMapPane = MAZE_CONTROLLER.makeMazeGrid(selectedMap);
        border.setCenter(selectedMapPane);

    }

    @FXML
    private void startGame() throws IOException {
        if (selectedMapPane != null) {
            GAME_CONTROLLER.startANewGame(selectedMapPane, selectedMap.clone(), hardDifficulty, healthSpinner.getValue());
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/Game.fxml"));
            StackPane gamePane = fxmlLoader.load();
            ((BorderPane) gamePane.getChildren().get(0)).setCenter(selectedMapPane);
            Stage stage = (Stage) border.getScene().getWindow();
            Scene scene = new Scene(gamePane);
            GameView gameView = fxmlLoader.getController();
            gameView.initializeKeyPressAndInfoBar(scene);
            stage.setScene(scene);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("no map chosen");
            alert.setHeaderText("please choose a map before starting the game");
            alert.show();
        }
    }

    @FXML
    private void cancel() throws IOException {
        Stage stage = (Stage) border.getScene().getWindow();
        Parent pane = FXMLLoader.load(getClass().getResource("/resources/Main.fxml"));
        Scene scene = new Scene(pane);
        stage.setScene(scene);
    }

    @FXML
    private void changeDifficulty() {
        hardDifficulty = !hardDifficulty;
    }
}
