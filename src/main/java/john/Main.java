package john;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import john.ui.MainWindow;

/**
 * Entry point for JavaFX UI.
 */
public class Main extends Application {
    private final John john = new John("data/johnChatBot.txt");

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("JohnChatBot");
            fxmlLoader.<MainWindow>getController().setJohn(john);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML file", e);
        }
    }
}
