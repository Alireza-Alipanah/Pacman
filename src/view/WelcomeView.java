package view;

import controller.UserController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;

import java.io.IOException;

public class WelcomeView {

    private static final UserController USER_CONTROLLER;
    private static final Music MUSIC;

    static {
        USER_CONTROLLER = UserController.getInstance();
        MUSIC = Music.getInstance();
    }

    @FXML
    private TextField username;
    @FXML
    private PasswordField password;

    public WelcomeView() {
        MUSIC.playBackground();
    }

    @FXML
    private void tryToRegister() {
        if (username.getText().isEmpty() || password.getText().isEmpty()) {
            Alert alert = makeAlert(Alert.AlertType.ERROR, "empty input", "please fill all the boxes");
            alert.show();
        } else if (USER_CONTROLLER.usernameAlreadyExists(username.getText())) {
            Alert alert = makeAlert(Alert.AlertType.ERROR, "wrong username", "this username already exists!");
            alert.show();
        } else {
            USER_CONTROLLER.registerUser(username.getText(), password.getText());
            Alert alert = makeAlert(Alert.AlertType.INFORMATION, "registered", "user registered successfully");
            alert.show();
        }
        password.setText("");
    }

    @FXML
    private void tryToLogin(ActionEvent event) throws IOException {
        if (username.getText().isEmpty() || password.getText().isEmpty()) {
            Alert alert = makeAlert(Alert.AlertType.ERROR, "empty input", "please fill all the boxes");
            alert.show();
        } else if (!USER_CONTROLLER.usernameAlreadyExists(username.getText())) {
            Alert alert = makeAlert(Alert.AlertType.ERROR, "wrong username", "no user with this username exists");
            alert.show();
        } else if (!USER_CONTROLLER.passwordIsRight(username.getText(), password.getText())) {
            Alert alert = makeAlert(Alert.AlertType.ERROR, "wrong password", "wrong password");
            alert.show();
        } else {
            Alert alert = makeAlert(Alert.AlertType.INFORMATION, "logged in", "logged in successfully");
            alert.showAndWait();
            User user = USER_CONTROLLER.getUserByUsername(username.getText());
            User.setLoggedIn(user);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent mainPain = FXMLLoader.load(getClass().getResource("/resources/Main.fxml"));
            Scene scene = new Scene(mainPain);
            stage.setScene(scene);
            stage.show();
        }
        password.setText("");
    }

    @FXML
    private void playAsGuest(ActionEvent event) throws IOException {
        Alert alert = makeAlert(Alert.AlertType.INFORMATION, "guest", "logged in as guest user");
        alert.setContentText("no record from guest users will be saved");
        alert.show();
        Parent mainPain = FXMLLoader.load(getClass().getResource("/resources/Main.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(mainPain);
        stage.setScene(scene);
        User.setLoggedIn(new User("", ""));
        stage.show();
    }

    private Alert makeAlert(Alert.AlertType type, String title, String header) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        return alert;
    }
}
