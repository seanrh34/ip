package john;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the main GUI
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private John john;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private Image dukeImage = new Image(this.getClass().getResourceAsStream("/images/DaJohn.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    public void setJohn(John j) {
        this.john = j;
        dialogContainer.getChildren().add(DialogBox.ofJohn("Hello! I'm JohnChatBot.\nWhat can I do for you?"));
    }

    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input == null || input.isBlank()) {
            return;
        }
        String response = john.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.ofUser(input),
                DialogBox.ofJohn(response)
        );
        userInput.clear();

        // Exit if the user typed 'bye'
        if ("bye".equalsIgnoreCase(input.trim())) {
            // let the farewell message render before closing
            Platform.runLater(() -> {
                userInput.getScene().getWindow().hide();
            });
        }
    }
}
