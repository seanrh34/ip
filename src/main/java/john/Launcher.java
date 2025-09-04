package john;

import javafx.application.Application;

/**
 * A separate launcher class to avoid classpath issues on some platforms.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
