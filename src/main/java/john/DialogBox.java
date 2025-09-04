package john;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Simple dialog bubble for user and John messages.
 */
public class DialogBox extends HBox {
    @FXML
    private Label text;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String message, Image avatar, boolean isUser) {
        super();
        Label lbl = new Label(message);
        lbl.setWrapText(true);
        lbl.setMaxWidth(380);

        ImageView iv = new ImageView(avatar);
        iv.setFitWidth(40);
        iv.setFitHeight(40);
        iv.setPreserveRatio(true);

        setSpacing(10);
        setPadding(new Insets(8));
        setAlignment(isUser ? Pos.TOP_RIGHT : Pos.TOP_LEFT);

        if (isUser) {
            getChildren().addAll(lbl, iv);
            getStyleClass().add("user-bubble");
        } else {
            getChildren().addAll(iv, lbl);
            getStyleClass().add("john-bubble");
        }
    }

    private static final Image USER = new Image(DialogBox.class.getResourceAsStream("/images/DaUser.png"));
    private static final Image JOHN = new Image(DialogBox.class.getResourceAsStream("/images/DaJohn.png"));

    public static DialogBox ofUser(String message) {
        return new DialogBox(message, USER, true);
    }

    public static DialogBox ofJohn(String message) {
        return new DialogBox(message, JOHN, false);
    }
}
