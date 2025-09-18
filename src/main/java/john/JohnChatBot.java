package john;

import john.command.Parser;
import john.data.Storage;
import john.data.TaskList;
import john.exceptions.JohnException;
import john.tasks.Task;
import john.ui.Ui;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Class to initialize the UI, Storage, and TaskList, and to start the chatbot application loop.
 */
public class JohnChatBot {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    /**
     * Function to initialize the chatbot with storage and load existing tasks if available.
     * Tasks will be saved into the hard disk under ./data/johnChatBot.txt
     *
     * @param filePath the path to the storage file as a string
     */
    public JohnChatBot(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(Path.of(filePath));
        TaskList loaded;
        try {
            loaded = new TaskList(storage.load());
        } catch (IOException e) {
            ui.showLine();
            ui.showError("Warning: Could not load saved tasks. Starting with an empty list.");
            ui.showLine();
            loaded = new TaskList();
        }
        this.tasks = loaded;
    }

    /**
     * Function to start the chatbot application.
     *
     * @param args the string input
     */
    public static void main(String[] args) {
        new JohnChatBot("data/johnChatBot.txt").run();
    }

    /**
     * Function to check if an index is from 0 to range.
     *
     * @param idx  the index to be checked
     * @param size the current number of tasks
     * @throws JohnException if the index is out of range
     */
    private static void ensureIndexInRange(int idx, int size) throws JohnException {
        if (idx < 0 || idx >= size) {
            throw new JohnException("Invalid index! Please enter a number between 1 and " + size);
        }
    }
    /**
     * Function to run the main application.
     * Reads user commands in a loop, parses them, and delegates handling
     * to command-specific helpers while managing UI output and persistence.
     */
    public void run() {
        ui.showWelcome();
        boolean isExit = false;

        while (!isExit && ui.hasNextLine()) {
            String fullCommand = ui.readCommand();
            try {
                Parser.Parsed p = Parser.parse(fullCommand);
                isExit = processCommand(p);
            } catch (JohnException e) {
                ui.showError(e.getMessage());
            } catch (IOException ioe) {
                ui.showError("Warning: Failed to save tasks to disk.");
            } finally {
                ui.showLine();
            }
        }
    }

    /**
     * Processes a parsed command by delegating to the appropriate handler.
     *
     * @param p The parsed command.
     * @return {@code true} if the command requests application exit; {@code false} otherwise.
     * @throws IOException   If persisting tasks fails in a handler that modifies tasks.
     * @throws JohnException If a command requires a valid index and it is out of range.
     */
    private boolean processCommand(Parser.Parsed p) throws IOException, JohnException {
        return switch (p.kind) {
        case EXIT -> handleExit();
        case LIST -> {
            handleList();
            yield false;
        }
        case ADD -> {
            handleAdd(p);
            yield false;
        }
        case MARK -> {
            handleMark(p);
            yield false;
        }
        case UNMARK -> {
            handleUnmark(p);
            yield false;
        }
        case FIND -> {
            handleFind(p);
            yield false;
        }
        case DELETE -> {
            handleDelete(p);
            yield false;
        }
        default -> false;
        };
    }

    /**
     * Handles the EXIT command by showing the goodbye message.
     *
     * @return {@code true} to signal that the application should exit.
     */
    private boolean handleExit() {
        ui.showGoodbye();
        return true;
    }

    /**
     * Handles the LIST command by rendering the current tasks.
     */
    private void handleList() {
        ui.showList(tasks);
    }

    /**
     * Handles the ADD command by adding a task and persisting the change.
     *
     * @param p Parsed command containing the task to add.
     * @throws IOException If saving the updated tasks fails.
     */
    private void handleAdd(Parser.Parsed p) throws IOException {
        tasks.add(p.task);
        ui.showAdded(p.task, tasks.size());
        saveTasks();
    }

    /**
     * Handles the MARK command by marking a task done and persisting the change.
     *
     * @param p Parsed command containing the zero-based index to mark.
     * @throws JohnException If the index is out of range.
     * @throws IOException   If saving the updated tasks fails.
     */
    private void handleMark(Parser.Parsed p) throws IOException, JohnException {
        ensureIndexInRange(p.index, tasks.size());
        Task t = tasks.mark(p.index);
        ui.showMarked(t);
        saveTasks();
    }

    /**
     * Handles the UNMARK command by marking a task not done and persisting the change.
     *
     * @param p Parsed command containing the zero-based index to unmark.
     * @throws JohnException If the index is out of range.
     * @throws IOException   If saving the updated tasks fails.
     */
    private void handleUnmark(Parser.Parsed p) throws IOException, JohnException {
        ensureIndexInRange(p.index, tasks.size());
        Task t = tasks.unmark(p.index);
        ui.showUnmarked(t);
        saveTasks();
    }

    /**
     * Handles the FIND command by showing tasks that match the query.
     *
     * @param p Parsed command containing the search query.
     */
    private void handleFind(Parser.Parsed p) {
        List<Task> matches = tasks.find(p.query);
        ui.showFound(matches);
    }

    /**
     * Handles the DELETE command by removing a task and persisting the change.
     *
     * @param p Parsed command containing the zero-based index to delete.
     * @throws JohnException If the index is out of range.
     * @throws IOException   If saving the updated tasks fails.
     */
    private void handleDelete(Parser.Parsed p) throws IOException, JohnException {
        ensureIndexInRange(p.index, tasks.size());
        Task removed = tasks.remove(p.index);
        ui.showDeleted(removed, tasks.size());
        saveTasks();
    }

    /**
     * Persists the current task list to storage.
     *
     * @throws IOException If writing to storage fails.
     */
    private void saveTasks() throws IOException {
        storage.save(tasks.asList());
    }

}
