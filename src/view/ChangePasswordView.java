package view;

import controller.UserController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import model.User;

public class ChangePasswordView {

    private static final UserController USER_CONTROLLER;

    static {
        USER_CONTROLLER = UserController.getInstance();
    }

    @FXML
    private PasswordField oldPassword;
    @FXML
    private PasswordField newPassword;

    @FXML
    private void changePassword() {
        if (oldPassword.getText().isEmpty() || newPassword.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("empty input");
            alert.setHeaderText("please fill all the boxes");
            alert.show();
        } else {
            User user = User.getLoggedIn();
            if (USER_CONTROLLER.passwordIsRight(user.getUsername(), oldPassword.getText())) {
                user.setPassword(newPassword.getText());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("success");
                alert.setHeaderText("password changed successfully");
                alert.show();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("wrong password");
                alert.setHeaderText("old password is wrong");
                alert.show();
            }
        }
        oldPassword.setText("");
        newPassword.setText("");
    }
}
