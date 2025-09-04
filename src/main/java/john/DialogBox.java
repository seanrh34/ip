package john;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.Collections;

/**
 * Simple dialog bubble for user and John messages.
 */
public class DialogBox extends HBox {

    private static final Image USER = new Image(DialogBox.class.getResourceAsStream("/images/you_dancing.gif"));
    private static final Image JOHN = new Image(DialogBox.class.getResourceAsStream("/images/john_dancing.gif"));

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

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

    private void flip() {
        dialog.getStyleClass().add("reply-label");
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
    }

    public static DialogBox ofUser(String message) {
        return new DialogBox(message, USER, true);
    }

    public static DialogBox ofJohn(String message) {
        DialogBox johnDb = new DialogBox(message, JOHN, false);
        johnDb.flip();
        return johnDb;
    }
}
