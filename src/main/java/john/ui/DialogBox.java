package john.ui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Represents a dialog bubble for displaying messages in the chat interface.
 * Contains both user and John chatbot messages with appropriate avatars and styling.
 */
public class DialogBox extends HBox {

    private static final Image USER = new Image(DialogBox.class.getResourceAsStream("/images/you_dancing.gif"));
    private static final Image JOHN = new Image(DialogBox.class.getResourceAsStream("/images/john_dancing.gif"));

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Constructs a DialogBox for the conversation with the chatbot GUI.
     * 
     * @param message The text message to display.
     * @param avatar The image to show as the avatar.
     * @param isUser True if this dialog box represents a user message, false for bot.
     */
    private DialogBox(String message, Image avatar, boolean isUser) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(message);
        displayPicture.setImage(avatar);
    }

    /**
     * Flips the chat bubble along the vertical axis for John-User chats
     */
    private void flip() {
        dialog.getStyleClass().add("reply-label");
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a new DialogBox for the user's message.
     * 
     * @param message The message sent by the user.
     * @return A DialogBox configured for user display.
     */
    public static DialogBox ofUser(String message) {
        return new DialogBox(message, USER, true);
    }

    /**
     * Creates a new DialogBox for John the chatbot.
     * 
     * @param message The message to be sent by John.
     * @return A DialogBox configured for John's display.
     */
    public static DialogBox ofJohn(String message) {
        DialogBox johnDb = new DialogBox(message, JOHN, false);
        johnDb.flip();
        return johnDb;
    }
}
