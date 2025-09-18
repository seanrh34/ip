package john;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import john.data.Storage;
import john.tasks.Deadline;
import john.tasks.Event;
import john.tasks.Task;
import john.tasks.ToDo;

/**
 * Class to test Storage save/load round-trip with tasks containing date-time fields.
 */
public class StorageTest {

    /**
     * Function to test that saving then loading reproduces the same tasks,
     * including done status and LocalDateTime values.
     */
    @Test
    @DisplayName("Storage round-trip: save then load returns equivalent tasks")
    void storage_roundTrip(@TempDir Path tmp) throws IOException {
        Path file = tmp.resolve("johnChatBot.txt");
        Storage storage = new Storage(file);

        // Build a sample list
        List<Task> tasks = new ArrayList<>();

        ToDo todo = new ToDo("read book");
        tasks.add(todo);

        Deadline dl = new Deadline("return book", LocalDateTime.of(2025, 8, 28, 18, 0));
        dl.mark(); // mark as done to test status persistence
        tasks.add(dl);

        Event ev = new Event("project meeting",
                LocalDateTime.of(2025, 8, 28, 9, 0),
                LocalDateTime.of(2025, 8, 28, 10, 30));
        tasks.add(ev);

        // Save then load
        storage.save(tasks);
        List<Task> loaded = storage.load();

        assertEquals(3, loaded.size(), "Task count should be preserved");

        // 1) ToDo
        assertInstanceOf(ToDo.class, loaded.get(0));
        assertEquals("read book", loaded.get(0).getDesc());
        assertFalse(loaded.get(0).getIsDone());

        // 2) Deadline
        assertInstanceOf(Deadline.class, loaded.get(1));
        Deadline loadedDl = (Deadline) loaded.get(1);
        assertEquals("return book", loadedDl.getDesc());
        assertTrue(loadedDl.getIsDone(), "Done status should persist");
        assertEquals(LocalDateTime.of(2025, 8, 28, 18, 0), loadedDl.getBy(),
                "Deadline time should persist");

        // 3) Event
        assertInstanceOf(Event.class, loaded.get(2));
        Event loadedEv = (Event) loaded.get(2);
        assertEquals("project meeting", loadedEv.getDesc());
        assertEquals(LocalDateTime.of(2025, 8, 28, 9, 0), loadedEv.getFrom());
        assertEquals(LocalDateTime.of(2025, 8, 28, 10, 30), loadedEv.getTo());
    }
}
