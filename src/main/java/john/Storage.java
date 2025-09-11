package john;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Class to help JohnChatBot manage its task history by storing them in the hard disk
 * The history will be stored as a txt file in ./data/johnChatBot.txt
 */
public class Storage {
    // Canonical storage format for all date-times: DD/MM/YYYY HHMM (single-digit day/month allowed)
    private static final DateTimeFormatter DMY_HM = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");
    private final Path file;

    /**
     * Function to create a new instance of Storage
     *
     * @param file path to the storage file
     */
    public Storage(Path file) {
        this.file = file;
    }

    /**
     * Remove labels like "By:", "From:", "To:" (case-insensitive) and return the value text.
     */
    private static String stripLabeled(String s, String label) {
        String trimmed = s.trim();
        if (trimmed.regionMatches(true, 0, label, 0, label.length())) {
            return trimColon(trimmed, label);
        }
        return trimmed;
    }

    /**
     * Helper for the stripLabelled function above
     * @param trimmed a string
     * @param label a string
     * @return a string that is trimmed if it starts with ':'
     */
    private static String trimColon(String trimmed, String label) {
        String rest = trimmed.substring(label.length()).trim();
        if (rest.startsWith(":")) {
            rest = rest.substring(1).trim();
        }
        return rest;
    }

    /**
     * Function to load the list of tasks from the .txt file into JohnChatBot,
     * converts the tasks from string to Task
     *
     * @return list of tasks
     * @throws IOException if the file cannot be read/created
     */
    public List<Task> load() throws IOException {
        // Ensure folder exists
        if (file.getParent() != null) {
            Files.createDirectories(file.getParent());
        }
        // Create and return empty list if file not found
        if (!Files.exists(file)) {
            Files.createFile(file);
            return new ArrayList<>();
        }

        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        List<Task> tasks = new ArrayList<>();

        for (String raw : lines) {
            parseTaskLine(raw).ifPresent(tasks::add);
        }

        return tasks;
    }

    /**
     * method to parse lines of Tasks (in String format) to conver to Task objects
     * @param raw lines in string, representing a Task
     * @return the Task Object obtained from converting the string
     */
    private static Optional<Task> parseTaskLine(String raw) {
        if (raw == null) {
            return Optional.empty();
        }
        String line = raw.strip();
        if (line.isEmpty()) {
            return Optional.empty();
        }

        String[] parts = line.split("\\s*\\|\\s*");
        if (parts.length < 3) {
            return Optional.empty();
        }

        String type = parts[0].strip();
        String status = parts[1].strip();
        String desc = parts[2].strip();

        boolean isDone = "Done".equalsIgnoreCase(status);

        Optional<Task> task = switch (type) {
        case "T" -> decodeTodo(desc);
        case "D" -> decodeDeadline(desc, parts);
        case "E" -> decodeEvent(desc, parts);
        default -> Optional.empty(); // unknown tag
        };

        task.ifPresent(t -> applyDoneFlag(t, isDone));
        return task;
    }
    /**
     * Function to decode a Todo in string representation (lines)
     * @param desc string for description of Todo
     * @return array of a string representation of Todo, separated
     */
    private static Optional<Task> decodeTodo(String desc) {
        if (desc.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new ToDo(desc));
    }
    /**
     * Function to decode a Deadline in string representation (lines)
     * @param desc string for description of Deadline
     * @param parts array of a string representation of Deadline, separated
     * @return
     */
    private static Optional<Task> decodeDeadline(String desc, String[] parts) {
        if (parts.length < 4 || desc.isEmpty()) {
            return Optional.empty();
        }
        String byStr = stripLabeled(parts[3], "By").strip();
        Optional<LocalDateTime> by = parseDate(byStr);
        if (by.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new Deadline(desc, by.get()));
    }

    /**
     * Function to decode an Event in string representation (lines)
     * @param desc string for description of Event
     * @param parts array of a string representation of Events, separated
     * @return Task version of the Event
     */
    private static Optional<Task> decodeEvent(String desc, String[] parts) {
        if (parts.length < 5 || desc.isEmpty()) {
            return Optional.empty();
        }
        String fromStr = stripLabeled(parts[3], "From").strip();
        String toStr = stripLabeled(parts[4], "To").strip();

        Optional<LocalDateTime> from = parseDate(fromStr);
        Optional<LocalDateTime> to = parseDate(toStr);
        if (from.isEmpty() || to.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new Event(desc, from.get(), to.get()));
    }
    /**
     * Converts a date from a string to LocalDateTime
     * @param s string representation of a date
     * @return LocalDateTime version
     */
    private static Optional<LocalDateTime> parseDate(String s) {
        try {
            return Optional.of(LocalDateTime.parse(s, DMY_HM));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }
    /**
     * mark a Task as done
     * @param t
     * @param isDone
     */
    private static void applyDoneFlag(Task t, boolean isDone) {
        if (isDone) {
            t.mark();
        }
    }
    /**
     * Function to save the current list of tasks in to the .txt file
     * To be called from JohnChatBot.java after any mutation in the tasks
     *
     * @param tasks list of tasks to persist
     * @throws IOException if writing fails
     */
    public void save(List<Task> tasks) throws IOException {
        Objects.requireNonNull(tasks, "tasks must not be null");

        List<String> out = tasks.stream()
                .map(Storage::encodeTaskLine)
                .toList();

        ensureParentDir();
        writeLines(out);
    }
    /**
     * Method to encode a task by converting it to string to be written onto a file
     * @param t a Task to be converted
     * @return A string to be written into the file
     */
    private static String encodeTaskLine(Task t) {
        Objects.requireNonNull(t, "task must not be null");
        String status = t.getIsDone() ? "Done" : "Not Done";

        if (t instanceof Deadline d) {
            return String.join(" | ",
                    "D",
                    status,
                    d.getDesc(),
                    "By: " + d.getBy().format(DMY_HM)
            );
        }
        if (t instanceof Event e) {
            return String.join(" | ",
                    "E",
                    status,
                    e.getDesc(),
                    "From: " + e.getFrom().format(DMY_HM),
                    "To: " + e.getTo().format(DMY_HM)
            );
        }
        return String.join(" | ", "T", status, t.getDesc());
    }
    /**
     * method to check if the directory exists, creates one otherwise
     * @throws IOException if input is invalid
     */
    private void ensureParentDir() throws IOException {
        Path parent = file.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
    }
    /**
     * Method for file writing to write new lines on the file
     * @param out a list of strings to write
     * @throws IOException if there is invalid input
     */
    private void writeLines(List<String> out) throws IOException {
        Files.write(
                file,
                out,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
        );
    }

}
