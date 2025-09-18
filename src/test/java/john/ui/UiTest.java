package john.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * This JUnit test is meant for the Ui.java, which is a fallback for if the GUI does not work
 */
public class UiTest {
    @Test
    @DisplayName("Ui.showWelcome prints welcome message to console")
    void showWelcome_printsMessage() {
        // Capture System.out
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(baos));

        Ui ui = new Ui();
        ui.showWelcome();

        System.out.flush();
        System.setOut(originalOut);

        String output = baos.toString();
        assertTrue(output.contains("Hello! I'm JohnChatBot"),
                "Output should contain welcome message");
    }
}
