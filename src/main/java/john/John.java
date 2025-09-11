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
     *
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

    private static void ensureIndexInRange(int idx, int size) throws JohnException {
        if (idx < 0 || idx >= size) {
            throw new JohnException("Invalid index! Please enter a number between 1 and " + size);
        }
    }

    /**
     * Produces a response string for the given user input.
     * Performs basic input checks, parses the command, and delegates
     * to command-specific handlers. Exceptions are converted to user-facing messages.
     *
     * @param input Raw user input; may be {@code null} or blank.
     * @return A response message to be shown to the user.
     */
    public String getResponse(String input) {
        String s = (input == null) ? "" : input.strip();
        if (s.isEmpty()) {
            return "John \uD83D\uDDFF demands you type something.";
        }

        try {
            Parser.Parsed p = Parser.parse(s);
            return switch (p.kind) {
            case EXIT -> handleExit();
            case LIST -> handleList();
            case ADD -> handleAdd(p);
            case MARK -> handleMark(p);
            case UNMARK -> handleUnmark(p);
            case DELETE -> handleDelete(p);
            case FIND -> handleFind(p);
            case HELP -> helpText();
            default -> "Unknown command, type in \"help\" for available commands. \uD83D\uDDFF";
            };
        } catch (JohnException je) {
            return je.getMessage();
        } catch (IOException ioe) {
            return "John \uD83D\uDDFF couldn't save your tasks: " + ioe.getMessage();
        } catch (Exception e) {
            return "Even John \uD83D\uDDFF did not expect this error: " + e.getMessage();
        }
    }

    /**
     * Returns the farewell message for the EXIT command.
     *
     * @return A goodbye string; note that UI decides actual shutdown.
     */
    private String handleExit() {
        // App doesn't exit here; MainWindow decides.
        return "Bye. John \uD83D\uDDFF WILL see YOU soon.";
    }

    /**
     * Renders the current task list for the LIST command.
     *
     * @return A string representation of all tasks.
     */
    private String handleList() {
        return tasks.toDisplayString();
    }

    /**
     * Handles adding a new task and persists the updated list.
     *
     * @param p Parsed command containing the task to add.
     * @return An acknowledgement message including the added task and new count.
     * @throws IOException If persisting the updated task list fails.
     */
    private String handleAdd(Parser.Parsed p) throws IOException {
        tasks.add(p.task);
        saveTasks();
        return "Acknowledge. John \uD83D\uDDFF has added this task:\n  " + p.task
                + "\nNow you have " + tasks.size()
                + " tasks in the list.";
    }

    /**
     * Marks a task as done and persists the change.
     *
     * @param p Parsed command containing the zero-based index to mark.
     * @return A confirmation message containing the marked task.
     * @throws JohnException If the index is out of range.
     * @throws IOException   If persisting the updated task list fails.
     */
    private String handleMark(Parser.Parsed p) throws IOException, JohnException {
        ensureIndexInRange(p.index, tasks.size());
        Task t = tasks.mark(p.index);
        saveTasks();
        return "Outstanding. John \uD83D\uDDFF has marked this task as 'done':\n  " + t;
    }

    /**
     * Marks a task as not done and persists the change.
     *
     * @param p Parsed command containing the zero-based index to unmark.
     * @return A confirmation message containing the unmarked task.
     * @throws JohnException If the index is out of range.
     * @throws IOException   If persisting the updated task list fails.
     */
    private String handleUnmark(Parser.Parsed p) throws IOException, JohnException {
        ensureIndexInRange(p.index, tasks.size());
        Task t = tasks.unmark(p.index);
        saveTasks();
        return "Understood, John \uD83D\uDDFF has marked this task as 'not done yet':\n  " + t;
    }

    /**
     * Deletes a task and persists the updated list.
     *
     * @param p Parsed command containing the zero-based index to delete.
     * @return A confirmation message including the removed task and new count.
     * @throws JohnException If the index is out of range.
     * @throws IOException   If persisting the updated task list fails.
     */
    private String handleDelete(Parser.Parsed p) throws IOException, JohnException {
        ensureIndexInRange(p.index, tasks.size());
        Task t = tasks.remove(p.index);
        saveTasks();
        return "Affirmative. John \uD83D\uDDFF has removed this task:\n  " + t
                + "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    /**
     * Finds tasks matching the supplied query string.
     *
     * @param p Parsed command containing the search query.
     * @return Either a rendered list of matches or a “no matches” message.
     */
    private String handleFind(Parser.Parsed p) {
        TaskList filtered = (TaskList) tasks.find(p.query);
        return filtered.size() == 0
                ? "John \uD83D\uDDFF can't find matching tasks for \"" + p.query + "\" and John is never wrong."
                : filtered.toDisplayString();
    }

    /**
     * Persists the current task list to storage.
     *
     * @throws IOException If writing to storage fails.
     */
    private void saveTasks() throws IOException {
        storage.save(tasks.asList());
    }

    /**
     * Returns the help text describing supported commands.
     *
     * @return A multi-line string enumerating available commands and usage.
     */
    private String helpText() {
        return String.join("\n",
                "Here are the commands you can use:",
                "",
                "General",
                "  help                          - Show this help",
                "  bye                           - Exit the chatbot",
                "",
                "Tasks",
                "  list                          - List all tasks",
                "  todo <description>            - Add a ToDo task",
                "  deadline <desc> /by <when>    - Add a Deadline task",
                "  event <desc> /from <start> /to <end> - Add an Event task",
                "",
                "Task status & editing",
                "  mark <n>                      - Mark task #n as done",
                "  unmark <n>                    - Mark task #n as not done",
                "  delete <n>                    - Delete task #n",
                "",
                "Search",
                "  find <keyword>                - Find tasks containing the keyword"
        );
    }

}
