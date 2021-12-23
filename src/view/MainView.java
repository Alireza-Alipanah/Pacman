package view;

import controller.GameController;
import controller.MazeController;
import controller.UserController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainView implements Initializable {

    private static final UserController USER_CONTROLLER;
    private static final MazeController MAZE_CONTROLLER;
    private static final Music MUSIC;
    private static final Image controlsImage;
    private static final ImageView controlImageView;

    static {
        USER_CONTROLLER = UserController.getInstance();
        MAZE_CONTROLLER = MazeController.getInstance();
        MUSIC = Music.getInstance();
        controlsImage = new Image("/resources/PNG/controls.png", 568, 631, true, true);
        controlImageView = new ImageView(controlsImage);
    }

    @FXML
    private BorderPane mainBorder;
    @FXML
    private ToolBar mainToolBar;
    @FXML
    private ToggleButton muteToggleButton;
    @FXML
    private CheckMenuItem smoothAnimationCheckbox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        muteToggleButton.setSelected(MUSIC.getVolume() == 0);
        MUSIC.playBackground();
        smoothAnimationCheckbox.setSelected(GameController.getSmoothAnimation());
    }

    @FXML
    private void changePassword() throws IOException {
        User user = User.getLoggedIn();
        if (!user.getUsername().isEmpty()) {
            Pane changePasswordPane = FXMLLoader.load(getClass().getResource("/resources/ChangePassword.fxml"));
            mainBorder.setCenter(changePasswordPane);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("guest");
            alert.setHeaderText("you have logged in as a guest");
            alert.show();
        }
    }

    @FXML
    private void deleteUser() throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        if (!User.getLoggedIn().getUsername().isEmpty()) {
            alert.setTitle("?");
            alert.setHeaderText("are you sure that you want to delete this account?");
            alert.setContentText("this action cant be undone");
            Optional<ButtonType> buttonPressed = alert.showAndWait();
            if (buttonPressed.isPresent())
                if (buttonPressed.get() == ButtonType.OK) {
                    USER_CONTROLLER.removeUser(User.getLoggedIn());
                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("success");
                    alert.setHeaderText("user deleted successfully");
                    alert.showAndWait();
                    logOut();
                }
        } else {
            alert.setTitle("guest user");
            alert.setHeaderText("you cant remove a guest user");
            alert.setContentText("do you want to exit to the welcome menu ?");
            Optional<ButtonType> buttonPressed = alert.showAndWait();
            if (buttonPressed.isPresent())
                if (buttonPressed.get() == ButtonType.OK)
                    logOut();
        }
    }

    @FXML
    private void logOut() throws IOException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("logged out");
        alert.setHeaderText("logged out successfully");
        alert.show();
        USER_CONTROLLER.logout();
        Pane root = FXMLLoader.load(getClass().getResource("/resources/Welcome.fxml"));
        Stage stage = (Stage) mainBorder.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void showScoreboard() {
        TableView<User> scoreBoard = new TableView<>();
        ArrayList<User> allUsersSorted = USER_CONTROLLER.getTenBestUsers();
        TableColumn<User, Integer> ranksColumn = new TableColumn<>("rank");
        ranksColumn.setCellValueFactory(new PropertyValueFactory<>("rank"));
        TableColumn<User, String> usernameColumn = new TableColumn<>("username");
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        TableColumn<User, Integer> scoreColumn = new TableColumn<>("score");
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        TableColumn<User, String> dateColumn = new TableColumn<>("date acquired");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("lastScoreTime"));
        scoreBoard.getColumns().add(ranksColumn);
        scoreBoard.getColumns().add(usernameColumn);
        scoreBoard.getColumns().add(scoreColumn);
        scoreBoard.getColumns().add(dateColumn);
        scoreBoard.getItems().addAll(allUsersSorted);
        ranksColumn.setPrefWidth(52.799986243247986);
        usernameColumn.setPrefWidth(218.39997100830078);
        scoreColumn.setPrefWidth(106.4000244140625);
        dateColumn.setPrefWidth(220.39999389648438);
        mainBorder.setCenter(scoreBoard);
    }

    @FXML
    private void makeANewMap() {
        int[][] maze = MAZE_CONTROLLER.makeANewMaze();
        GridPane mazeGrid = MAZE_CONTROLLER.makeMazeGrid(maze);
        mainBorder.setCenter(mazeGrid);
        ToolBar toolBar = new ToolBar();
        Button nextButton = new Button();
        nextButton.setText("next");
        nextButton.setOnAction(event -> makeANewMap());
        Button saveButton = new Button();
        saveButton.setText("save");
        saveButton.setOnAction(event -> {
            if (USER_CONTROLLER.userHaveAlreadySavedThisMap(maze)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("already saved");
                alert.setHeaderText("you have already saved this map");
                alert.show();
            } else {
                USER_CONTROLLER.saveMap(maze);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("saved");
                alert.setHeaderText("map saved");
                alert.show();
            }
        });
        Button backButton = new Button();
        backButton.setText("back");
        backButton.setOnAction(event -> {
            mainBorder.setCenter(null);
            mainBorder.setTop(mainToolBar);
        });
        toolBar.getItems().addAll(nextButton, saveButton, backButton);
        mainBorder.setTop(toolBar);
    }

    @FXML
    private void startNewGame() throws IOException {
        if (USER_CONTROLLER.loggedInUserHasMap()) {
            Stage stage = (Stage) mainBorder.getScene().getWindow();
            Parent newPane = FXMLLoader.load(getClass().getResource("/resources/NewGame.fxml"));
            Scene scene = new Scene(newPane);
            stage.setScene(scene);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("no map");
            alert.setHeaderText("you have no map to play, please generate one from it's menu");
            alert.setContentText("you can generate map from play -> generate map");
            alert.show();
        }
    }

    @FXML
    private void resumeGame() throws IOException {
        if (USER_CONTROLLER.userHasSavedGame()) {
            Stage stage = (Stage) mainBorder.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/Game.fxml"));
            Parent newPane = fxmlLoader.load();
            ((BorderPane) ((StackPane) newPane).getChildren().get(0)).setCenter(USER_CONTROLLER.getSavedUserGame());
            Scene scene = new Scene(newPane);
            GameView gameView = fxmlLoader.getController();
            gameView.initializeKeyPressAndInfoBar(scene);
            stage.setScene(scene);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("no save");
            alert.setHeaderText("you have no saved game to resume");
            alert.show();
        }
    }

    @FXML
    private void showControls() {
//        Label controls = new Label("UP : move upward\nDOWN : move down\nRIGHT : move right\nLEFT : move left\n" +
//                "SPACE : pause and unpause\nESCAPE : save and exit the game");
//        System.out.println(controls.getText());
//        mainBorder.setCenter(controls);
        mainBorder.setCenter(controlImageView);
    }

    @FXML
    private void mute() {
        if (muteToggleButton.isSelected())
            MUSIC.changeVolume(0);
        else MUSIC.changeVolume(0.5);
    }

    @FXML
    private void changeAnimationSmoothness() {
        GameController.setSmoothAnimation(!GameController.getSmoothAnimation());
        smoothAnimationCheckbox.setSelected(GameController.getSmoothAnimation());
        GameController.getInstance().adjustToNewSettings();
    }

}
