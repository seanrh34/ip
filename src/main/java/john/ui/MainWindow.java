package john.ui;

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
import john.John;

/**
 * Controller for the main GUI window of the John ChatBot application.
 * Manages user input, displays chat messages, and handles application shutdown.
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
     * Overlay to provide shutdown so that shutdown is not too abrupt after typing in "bye".
     * Ensures an overlay exists and is visible; builds and attaches it on first use.
     */
    private void showShutdownOverlay() {
        if (shutdownOverlay != null) {
            showOverlay(shutdownOverlay);
            return;
        }
        StackPane glass = buildShutdownOverlay();
        attachOverlayToRoot(glass);
        shutdownOverlay = glass;
        showOverlay(shutdownOverlay);
    }

    /**
     * Builds the full-screen glass overlay containing a centered shutdown card.
     *
     * @return a {@link StackPane} that acts as the glass overlay.
     */
    private StackPane buildShutdownOverlay() {
        StackPane glass = new StackPane();
        glass.setStyle("-fx-background-color: rgba(0,0,0,0.35);");
        glass.setPickOnBounds(true);
        glass.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

        VBox card = buildShutdownCard();
        StackPane.setAlignment(card, Pos.CENTER);
        glass.getChildren().add(card);

        return glass;
    }

    /**
     * Builds the shutdown card containing a spinner and a status label.
     *
     * @return a {@link VBox} node representing the shutdown card.
     */
    private VBox buildShutdownCard() {
        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setPrefSize(48, 48);

        Label msg = new Label("Shutting down…");
        msg.setStyle("-fx-font-size: 14px;");

        HBox row = new HBox(12, spinner, msg);
        row.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(row);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(24));
        card.setStyle(
                "-fx-background-color: -fx-base; -fx-background-radius: 12;"
                        + " -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.25), 12, 0, 0, 4);"
        );
        return card;
    }

    /**
     * Attaches the given overlay to the scene root and stretches it to full size.
     *
     * @param overlay the overlay to attach; must not be {@code null}.
     */
    private void attachOverlayToRoot(StackPane overlay) {
        if (overlay == null) {
            return;
        }
        if (scrollPane.getScene() == null || scrollPane.getScene().getRoot() == null) {
            // Scene not ready; nothing to attach to.
            return;
        }
        Pane root = (Pane) scrollPane.getScene().getRoot();

        if (root instanceof AnchorPane) {
            AnchorPane.setTopAnchor(overlay, 0.0);
            AnchorPane.setRightAnchor(overlay, 0.0);
            AnchorPane.setBottomAnchor(overlay, 0.0);
            AnchorPane.setLeftAnchor(overlay, 0.0);
        }
        root.getChildren().add(overlay);
    }

    /**
     * Makes the given overlay visible and managed so it participates in layout.
     *
     * @param overlay the overlay node.
     */
    private void showOverlay(Node overlay) {
        if (overlay == null) {
            return;
        }
        overlay.setVisible(true);
        if (overlay instanceof Region r) {
            r.setManaged(true);
        } else if (overlay.getParent() instanceof Region pr) {
            // Fallback: ensure parent layouts include the overlay.
            pr.setManaged(true);
        }
    }

    /**
     * Sets the John instance for this window and displays a welcome message.
     * @param j The John chatbot instance to associate with this window.
     */
    public void setJohn(John j) {
        this.john = j;
        dialogContainer.getChildren().add(DialogBox.ofJohn("John ChatBot \uD83D\uDDFF has arrived.\n"
                + "What can John \uD83D\uDDFF do for you?"));
    }

    /**
     * Handles user input from the text field (Send button / Enter key).
     * Validates input, delegates to John for a response, updates the dialog UI,
     * and initiates graceful shutdown when the exit command is detected.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String trimmed = normalizeInput(input);
        if (trimmed.isEmpty()) {
            return;
        }

        String response = john.getResponse(input);
        appendConversation(input, response);
        clearUserInput();

        if (isExitCommand(trimmed)) {
            beginGracefulShutdown();
        }
    }

    /**
     * Normalizes raw input by converting {@code null} to empty and stripping whitespace.
     *
     * @param s raw input which may be {@code null}.
     * @return a non-null, trimmed string (possibly empty).
     */
    private String normalizeInput(String s) {
        return (s == null) ? "" : s.strip();
    }

    /**
     * Appends the user message and JohnChatBot response to the dialog container.
     *
     * @param userMessage   the original user input.
     * @param johnResponse  the response returned by JohnChatBot.
     */
    private void appendConversation(String userMessage, String johnResponse) {
        dialogContainer.getChildren().addAll(
                DialogBox.ofUser(userMessage),
                DialogBox.ofJohn(johnResponse)
        );
    }

    /**
     * Clears the user input field.
     */
    private void clearUserInput() {
        userInput.clear();
    }

    /**
     * Determines whether the given (trimmed) input is the exit command.
     *
     * @param s normalized (trimmed) input string.
     * @return {@code true} if the input equals "bye" (case-insensitive); otherwise {@code false}.
     */
    private boolean isExitCommand(String s) {
        return "bye".equalsIgnoreCase(s);
    }

    /**
     * Begins a graceful shutdown sequence:
     * disables inputs, shows the shutdown overlay, and closes the window after a short delay.
     */
    private void beginGracefulShutdown() {
        disableInputControls();
        showShutdownOverlay();
        scheduleCloseAfter(Duration.seconds(1));
    }

    /**
     * Disables the input field and send button to prevent further interaction.
     */
    private void disableInputControls() {
        userInput.setDisable(true);
        sendButton.setDisable(true);
    }

    /**
     * Schedules closing of the application window after the specified duration.
     *
     * @param delay the delay before closing the window.
     */
    private void scheduleCloseAfter(Duration delay) {
        PauseTransition pt = new PauseTransition(delay);
        pt.setOnFinished(ev -> {
            Stage stage = getStage();
            if (stage != null) {
                stage.close();
            }
        });
        pt.play();
    }

    /**
     * Obtains the current stage from the send button's scene.
     *
     * @return the {@link Stage} associated with this window, or {@code null} if unavailable.
     */
    private Stage getStage() {
        return (sendButton != null && sendButton.getScene() != null)
                ? (Stage) sendButton.getScene().getWindow()
                : null;
    }
}
