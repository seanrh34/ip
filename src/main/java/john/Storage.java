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

/**
 * Class to help JohnChatBot manage its task history by storing them in the hard disk
 * The history will be stored as a txt file in ./data/johnChatBot.txt
 */
public class Storage {
    private final Path file;

    // Canonical storage format for all date-times: DD/MM/YYYY HHMM (single-digit day/month allowed)
    private static final DateTimeFormatter DMY_HM = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");

    /**
     * Function to create a new instance of Storage
     * @param file path to the storage file
     */
    public Storage(Path file) {
        assert file != null : "Storage file path must not be null";
        this.file = file;
    }

    /**
     * Function to load the list of tasks from the .txt file into JohnChatBot,
     * converts the tasks from string to Task
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
            String line = raw.trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split("\\s*\\|\\s*");
            if (parts.length < 3) {
                continue;
            }

            String type = parts[0];
            String status = parts[1];
            String desc = parts[2];
            boolean isDone = "Done".equalsIgnoreCase(status);

            try {
                switch (type) {
                case "T": {
                    Task t = new ToDo(desc);
                    if (isDone) {
                        t.mark();
                    }
                    tasks.add(t);
                    break;
                }
                case "D": {
                    if (parts.length < 4) {
                        break;
                    }
                    String byStr = stripLabeled(parts[3], "By");

                    LocalDateTime by;
                    try {
                        by = LocalDateTime.parse(byStr, DMY_HM);
                    } catch (DateTimeParseException e1) {
                        // Invalid date/time format parsed
                        break;
                    }

                    Task t = new Deadline(desc, by);
                    if (isDone) {
                        t.mark();
                    }
                    tasks.add(t);
                    break;
                }
                case "E": {
                    if (parts.length < 5) {
                        break;
                    }
                    String fromStr = stripLabeled(parts[3], "From");
                    String toStr = stripLabeled(parts[4], "To");

                    LocalDateTime from;
                    LocalDateTime to;

                    try {
                        from = LocalDateTime.parse(fromStr, DMY_HM);
                        to = LocalDateTime.parse(toStr, DMY_HM);
                    } catch (DateTimeParseException e1) {
                        // Invalid date/time format parsed
                        break;
                    }

                    Task t = new Event(desc, from, to);
                    if (isDone) {
                        t.mark();
                    }
                    tasks.add(t);
                    break;
                }
                default:
                    // Unknown type tag; skip
                    break;
                }
            } catch (Exception ex) {
                System.out.println("Error parsing file!");
            }
        }

        return tasks;
    }

    /**
     * Function to save the current list of tasks in to the .txt file
     * To be called from JohnChatBot.java after any mutation in the tasks
     * @param tasks list of tasks to persist
     * @throws IOException if writing fails
     */
    public void save(List<Task> tasks) throws IOException {
        assert tasks != null : "Tasks to be saved must not be null";
        List<String> out = new ArrayList<>(tasks.size());

        for (Task t : tasks) {
            assert t != null : "Cannot save a null task";
            String status = t.getIsDone() ? "Done" : "Not Done";

            if (t instanceof ToDo) {
                out.add(String.join(" | ",
                        "T",
                        status,
                        t.getDesc()
                ));
            } else if (t instanceof Deadline) {
                Deadline d = (Deadline) t;
                LocalDateTime by = d.getBy(); // assumes getter exists
                String byStr = by.format(DMY_HM);

                out.add(String.join(" | ",
                        "D",
                        status,
                        d.getDesc(),
                        "By: " + byStr
                ));
            } else if (t instanceof Event) {
                Event e = (Event) t;
                LocalDateTime from = e.getFrom(); // assumes getters exist
                LocalDateTime to = e.getTo();

                String fromStr = from.format(DMY_HM);
                String toStr = to.format(DMY_HM);

                out.add(String.join(" | ",
                        "E",
                        status,
                        e.getDesc(),
                        "From: " + fromStr,
                        "To: " + toStr
                ));
            } else {
                // Fallback: write as ToDo-style line with whatever description exists
                out.add(String.join(" | ", "T", status, t.getDesc()));
            }
        }

        if (file.getParent() != null) {
            Files.createDirectories(file.getParent());
        }

        Files.write(
                file,
                out,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
        );
    }

    /**
     * Remove labels like "By:", "From:", "To:" (case-insensitive) and return the value text.
     */
    private static String stripLabeled(String s, String label) {
        String trimmed = s.trim();
        // Accept "Label:" or "Label" (case-insensitive)
        if (trimmed.regionMatches(true, 0, label, 0, label.length())) {
            String rest = trimmed.substring(label.length()).trim();
            if (rest.startsWith(":")) {
                rest = rest.substring(1).trim();
            }
            return rest;
        }
        return trimmed;
    }
}
