import controller.SaveController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/resources/Welcome.fxml"));
        primaryStage.setTitle("Pacman");
        primaryStage.setScene(new Scene(root));
        primaryStage.setWidth(618);
        primaryStage.setHeight(681);
        primaryStage.getIcons().add(new Image("/resources/PNG/icon.png", 20, 20, true, false));
        primaryStage.setOnShowing(windowEvent -> {
            try {
                SaveController.getInstance().load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        primaryStage.setOnCloseRequest(windowEvent -> {
            try {
                SaveController.getInstance().save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}