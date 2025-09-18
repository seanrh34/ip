package john;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import john.command.Parser;
import john.exceptions.JohnException;
import john.tasks.Deadline;
import john.tasks.Event;

/**
 * Class to test Parser behavior for valid and invalid commands, including strict date-time parsing.
 */
public class ParserTest {

    /**
     * Function to test that a valid deadline command is parsed into a Deadline task
     * and the date-time is interpreted using DD/MM/YYYY HHMM strictly.
     */
    @Test
    @DisplayName("deadline parse: valid command yields Deadline with expected time")
    void parse_deadline_valid() throws Exception {
        Parser.Parsed p = Parser.parse("deadline return book /by 28/8/2025 1800");
        assertEquals(Parser.Parsed.Kind.ADD, p.kind, "Expected an ADD action");
        assertInstanceOf(Deadline.class, p.task, "Expected a Deadline task");

        Deadline d = (Deadline) p.task;
        LocalDateTime expected = LocalDateTime.of(2025, 8, 28, 18, 0);
        assertEquals(expected, d.getBy(), "Parsed LocalDateTime mismatch");
        assertEquals("return book", d.getDesc());
        assertFalse(d.getIsDone());
    }

    /**
     * Function to test that a valid event command yields an Event task with correct from/to times.
     */
    @Test
    @DisplayName("event parse: valid command yields Event with from/to times")
    void parse_event_valid() throws Exception {
        Parser.Parsed p = Parser.parse("event standup /from 28/8/2025 0900 /to 28/8/2025 0930");
        assertEquals(Parser.Parsed.Kind.ADD, p.kind);
        assertInstanceOf(Event.class, p.task, "Expected an Event task");

        Event e = (Event) p.task;
        assertEquals("standup", e.getDesc());
        assertEquals(LocalDateTime.of(2025, 8, 28, 9, 0), e.getFrom());
        assertEquals(LocalDateTime.of(2025, 8, 28, 9, 30), e.getTo());
    }

    /**
     * Function to test that invalid date formats for deadline are rejected with a helpful message.
     */
    @Test
    @DisplayName("deadline parse: several invalid date formats throw JohnException")
    void parse_deadline_invalidFormats() {
        String[] bad = new String[] {
            "deadline x /by 2025-08-28 1800", // ISO format, not allowed
            "deadline x /by 28/08/2025", // missing time
            "deadline x /by 28-8-2025 1800", // wrong separators
            "deadline x /by 32/8/2025 1800" // impossible day
        };
        for (String cmd : bad) {
            JohnException ex = assertThrows(JohnException.class, () -> Parser.parse(cmd), cmd);
            assertTrue(ex.getMessage().toLowerCase().contains("dd/mm/yyyy hhmm"),
                    "Error should mention required format: " + cmd);
        }
    }

    /**
     * Function to test that an event missing a /to part is rejected.
     */
    @Test
    @DisplayName("event parse: missing /to part throws JohnException")
    void parse_event_missingTo() {
        JohnException ex = assertThrows(JohnException.class, () -> Parser.parse(
                "event meet /from 28/8/2025 0900"));
        assertTrue(ex.getMessage().toLowerCase().contains("usage")
                        || ex.getMessage().toLowerCase().contains("requires"),
                "Error should include usage/help text");
    }
}
