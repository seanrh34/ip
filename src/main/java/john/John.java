package john;

import java.io.IOException;
import java.nio.file.Path;

/**
 * GUI-facing facade that maintains state and returns textual responses.
 */
public class John {
    private final Storage storage;
    private final TaskList tasks;

    /**
     * John class
     * @param filePath
     */
    public John(String filePath) {
        this.storage = new Storage(Path.of(filePath));
        TaskList loaded;
        try {
            loaded = new TaskList(storage.load());
        } catch (IOException e) {
            loaded = new TaskList(java.util.List.of());
        }
        this.tasks = loaded;
    }

    /**
     * Process a single line of user input and return JohnChatBot's response as a string.
     */
    public String getResponse(String input) {
        if (input == null) {
            return "Please type something 🙂";
        }
        String fullCommand = input.strip();
        if (fullCommand.isEmpty()) {
            return "Please type something 🙂";
        }
        try {
            Parser.Parsed p = Parser.parse(fullCommand);
            switch (p.kind) {
            case EXIT:
                // We don't exit the app here; MainWindow will decide whether to close the stage.
                return "Bye. Hope to see you again soon!";
            case LIST:
                return tasks.toDisplayString();
            case ADD:
                tasks.add(p.task);
                storage.save(tasks.asList());
                return "Got it. I've added this task:\n  " + p.task + "\nNow you have " + tasks.size()
                        + " tasks in the list.";
            case MARK: {
                ensureIndexInRange(p.index, tasks.size());
                Task t = tasks.mark(p.index);
                storage.save(tasks.asList());
                return "Nice! I've marked this task as done:\n  " + t;
            }
            case UNMARK: {
                ensureIndexInRange(p.index, tasks.size());
                Task t = tasks.unmark(p.index);
                storage.save(tasks.asList());
                return "OK, I've marked this task as not done yet:\n  " + t;
            }
            case DELETE: {
                ensureIndexInRange(p.index, tasks.size());
                Task t = tasks.remove(p.index);
                storage.save(tasks.asList());
                return "Noted. I've removed this task:\n  " + t + "\nNow you have " + tasks.size()
                        + " tasks in the list.";
            }
            case FIND: {
                TaskList filtered = (TaskList) tasks.find(p.query);
                return filtered.size() == 0
                    ? "No matching tasks found for \"" + p.query + "\"."
                    : filtered.toDisplayString();
            }
            default:
                return "Sorry, I didn't understand that.";
            }
        } catch (JohnException je) {
            return je.getMessage();
        } catch (IOException ioe) {
            return "⚠️ Oops, I couldn't save your tasks: " + ioe.getMessage();
        } catch (Exception e) {
            return "Unexpected error: " + e.getMessage();
        }
    }

    private static void ensureIndexInRange(int idx, int size) throws JohnException {
        if (idx < 0 || idx >= size) {
            throw new JohnException("Invalid index! Please enter a number between 1 and " + size);
        }
    }
}
