import java.io.IOException;
import java.nio.file.Path;

/**
 * Class to wire up the UI, Storage, and TaskList, and to run the chatbot application loop.
 */
public class JohnChatBot {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    /**
     * Function to initialize the chatbot with storage and load existing tasks if available.
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
     * @param args
     */
    public static void main(String[] args) {
        new JohnChatBot("data/johnChatBot.txt").run();
    }

    /**
     * Function to run the main application
     */
    public void run() {
        ui.showWelcome();
        boolean isExit = false;

        while (!isExit && ui.hasNextLine()) {
            String fullCommand = ui.readCommand();
            try {
                Parser.Parsed p = Parser.parse(fullCommand);
                ui.showLine();

                switch (p.kind) {
                    case EXIT:
                        ui.showGoodbye();
                        isExit = true;
                        break;

                    case LIST:
                        ui.showList(tasks);
                        break;

                    case ADD:
                        tasks.add(p.task);
                        ui.showAdded(p.task, tasks.size());
                        storage.save(tasks.asList());
                        break;

                    case MARK: {
                        ensureIndexInRange(p.index, tasks.size());
                        Task t = tasks.mark(p.index);
                        ui.showMarked(t);
                        storage.save(tasks.asList());
                        break;
                    }

                    case UNMARK: {
                        ensureIndexInRange(p.index, tasks.size());
                        Task t = tasks.unmark(p.index);
                        ui.showUnmarked(t);
                        storage.save(tasks.asList());
                        break;
                    }

                    case DELETE: {
                        ensureIndexInRange(p.index, tasks.size());
                        Task removed = tasks.remove(p.index);
                        ui.showDeleted(removed, tasks.size());
                        storage.save(tasks.asList());
                        break;
                    }
                }
            } catch (JohnException e) {
                ui.showLine();
                ui.showError(e.getMessage());
            } catch (IOException ioe) {
                ui.showLine();
                ui.showError("Warning: Failed to save tasks to disk.");
            } finally {
                ui.showLine();
            }
        }
    }

    /**
     * Function to check if an index is from 0 to range.
     * @param idx the index to be checked
     * @param size the current number of tasks
     * @throws JohnException if the index is out of range
     */
    private static void ensureIndexInRange(int idx, int size) throws JohnException {
        if (idx < 0 || idx >= size) {
            throw new JohnException("Invalid index! Please enter a number between 1 and " + size);
        }
    }
}