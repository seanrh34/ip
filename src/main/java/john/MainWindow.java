package john;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;


/**
 * Controller for the main GUI
 */
@SuppressWarnings("checkstyle:Regexp")
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

    private Node shutdownOverlay;

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }
    /**
     * Overlay to provide shutdown so that shutdown is not too abrupt after typing in "bye"
     */
    private void showShutdownOverlay() {
        if (shutdownOverlay != null) {
            shutdownOverlay.setVisible(true);
            shutdownOverlay.setManaged(true);
            return;
        }
        // Full-size glass pane
        StackPane glass = new StackPane();
        glass.setStyle("-fx-background-color: rgba(0,0,0,0.35);");
        glass.setPickOnBounds(true);
        glass.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

        // Card with spinner + label
        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setPrefSize(48, 48);

        Label msg = new Label("Shutting down…");
        msg.setStyle("-fx-font-size: 14px;");

        HBox row = new HBox(12, spinner, msg);
        row.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(row);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(24));
        card.setStyle("-fx-background-color: -fx-base; -fx-background-radius: 12;"
                + " -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.25), 12, 0, 0, 4);");

        StackPane.setAlignment(card, Pos.CENTER);
        glass.getChildren().add(card);

        // Add overlay to the scene root and stretch to full size
        Pane root = (Pane) scrollPane.getScene().getRoot();
        if (root instanceof AnchorPane) {
            AnchorPane.setTopAnchor(glass, 0.0);
            AnchorPane.setRightAnchor(glass, 0.0);
            AnchorPane.setBottomAnchor(glass, 0.0);
            AnchorPane.setLeftAnchor(glass, 0.0);
        }
        root.getChildren().add(glass);

        shutdownOverlay = glass;
    }


    public void setJohn(John j) {
        this.john = j;
        dialogContainer.getChildren().add(DialogBox.ofJohn("John ChatBot \uD83D\uDDFF has arrived.\n"
                + "What can John \uD83D\uDDFF do for you?"));
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

        String trimmed = input == null ? "" : input.strip();
        if (trimmed.equalsIgnoreCase("bye")) {
            userInput.setDisable(true);
            sendButton.setDisable(true);

            showShutdownOverlay();

            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished(ev -> {
                Stage stage = (Stage) sendButton.getScene().getWindow();
                stage.close();
            });
            delay.play();
        }

    }
}
