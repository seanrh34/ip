import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to help JohnChatBot manage its task history by storing them in the hard disk
 */
public class Storage {
    private final Path file;

    /**
     * Function to create a new instance of Storage
     * @param file
     */
    public Storage(Path file) {
        this.file = file;
    }

    /**
     * Function to load the list of tasks from the .txt file into JohnChatBot,
     * converts the tasks from string to Task
     * @return
     * @throws IOException
     */
    public List<Task> load() throws IOException {
        // Check if folder exist
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
            if (line.isEmpty()) continue;

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
                        if (isDone) t.mark();
                        tasks.add(t);
                        break;
                    }
                    case "D": {
                        if (parts.length < 4) {
                            continue;
                        }
                        String by = stripLabeled(parts[3], "By:");
                        Task t = new Deadline(desc, by);
                        if (isDone) t.mark();
                        tasks.add(t);
                        break;
                    }
                    case "E": {
                        if (parts.length < 5) {
                            continue;
                        }
                        String from = stripLabeled(parts[3], "From:");
                        String to   = stripLabeled(parts[4], "To:");
                        Task t = new Event(desc, from, to);
                        if (isDone) t.mark();
                        tasks.add(t);
                        break;
                    }
                    default:
                        // Skips if encountered an invalid tag
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
     * @param tasks
     * @throws IOException
     */
    public void save(List<Task> tasks) throws IOException {
        List<String> out = new ArrayList<>(tasks.size());

        for (Task t : tasks) {
            out.add(t.toFileFormatString());
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
     * Function to the labels on the .txt lines such as "from", "by", "to", etc
     * @param s
     * @param label
     * @return
     */
    private static String stripLabeled(String s, String label) {
        String trimmed = s.trim();
        if (trimmed.regionMatches(true, 0, label, 0, label.length())) {
            String rest = trimmed.substring(label.length()).trim();
            if (rest.startsWith(":")) rest = rest.substring(1).trim();
            return rest;
        }
        return trimmed;
    }
}