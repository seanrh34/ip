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
            return "John \uD83D\uDDFF demands you type something.";
        }
        String fullCommand = input.strip();
        if (fullCommand.isEmpty()) {
            return "John \uD83D\uDDFF demands you type something.";
        }
        try {
            Parser.Parsed p = Parser.parse(fullCommand);
            switch (p.kind) {
            case EXIT:
                // App doesn't exit here, MainWindow will decide whether to close the stage.
                return "Bye. John \uD83D\uDDFF WILL see YOU soon.";
            case LIST:
                return tasks.toDisplayString();
            case ADD:
                tasks.add(p.task);
                storage.save(tasks.asList());
                return "Acknowledge. John \uD83D\uDDFF has added this task:\n  " + p.task
                        + "\nNow you have " + tasks.size()
                        + " tasks in the list.";
            case MARK: {
                ensureIndexInRange(p.index, tasks.size());
                Task t = tasks.mark(p.index);
                storage.save(tasks.asList());
                return "Outstanding. John \uD83D\uDDFF has marked this task as 'done':\n  " + t;
            }
            case UNMARK: {
                ensureIndexInRange(p.index, tasks.size());
                Task t = tasks.unmark(p.index);
                storage.save(tasks.asList());
                return "Understood, John \uD83D\uDDFF has marked this task as 'not done yet':\n  " + t;
            }
            case DELETE: {
                ensureIndexInRange(p.index, tasks.size());
                Task t = tasks.remove(p.index);
                storage.save(tasks.asList());
                return "Affirmative. John \uD83D\uDDFF has removed this task:\n  " + t + "\nNow you have "
                        + tasks.size() + " tasks in the list.";
            }
            case FIND: {
                TaskList filtered = (TaskList) tasks.find(p.query);
                return filtered.size() == 0
                        ? "John \uD83D\uDDFF can't find matching tasks for \"" + p.query
                        + "\" and John is never wrong."
                        : filtered.toDisplayString();
            }
            case HELP:
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
            case UNKNOWN:
            default:
                return "Unknown command, type in \"help\" for available commands. \uD83D\uDDFF";
            }
        } catch (JohnException je) {
            return je.getMessage();
        } catch (IOException ioe) {
            return "John \uD83D\uDDFF couldn't save your tasks: " + ioe.getMessage();
        } catch (Exception e) {
            return "Even John \uD83D\uDDFF did not expect this error: " + e.getMessage();
        }
    }

    private static void ensureIndexInRange(int idx, int size) throws JohnException {
        if (idx < 0 || idx >= size) {
            throw new JohnException("Invalid index! Please enter a number between 1 and " + size);
        }
    }
}
