package john.ui;

import java.util.List;
import java.util.Scanner;

import john.data.TaskList;
import john.tasks.Task;

/**
 * Class to handle all user interactions such as printing messages and reading commands.
 * Note that this class is just a fallback for the GUI does not work.
 * It is not actually the main form of user interface of this app.
 */
public class Ui {
    private static final String DIVIDER = "=================================================\n";
    private final Scanner sc = new Scanner(System.in);

    /**
     * Function to print the welcome message at program start.
     */
    public void showWelcome() {
        System.out.print(DIVIDER);
        System.out.println("Hello! I'm JohnChatBot.");
        System.out.println("What can I do for you?");
        System.out.print(DIVIDER);
    }

    /**
     * Function to print the goodbye message before program exits.
     */
    public void showGoodbye() {
        System.out.print(DIVIDER);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.print(DIVIDER);
    }

    /**
     * Function to print a divider line for UI readability.
     */
    public void showLine() {
        System.out.print(DIVIDER);
    }

    /**
     * Function to read the next command line from user input.
     *
     * @return the raw string command entered by the user
     */
    public String readCommand() {
        return sc.hasNextLine() ? sc.nextLine() : "";
    }

    /**
     * Function to check if there is more input available from the user.
     *
     * @return true if more input exists, otherwise false
     */
    public boolean hasNextLine() {
        return sc.hasNextLine();
    }

    /**
     * Function to print an error message to the user.
     *
     * @param message the error message to display
     */
    public void showError(String message) {
        System.out.print("Error: " + message);
    }

    /**
     * Function to print the results of a find operation.
     * If no tasks match, a friendly message is shown.
     *
     * @param matches the list of tasks that matched the query
     */
    public void showFound(List<Task> matches) {
        if (matches.isEmpty()) {
            System.out.println("No matching tasks found.");
            return;
        }
        System.out.println("Here are the matching tasks in your list:\n");
        for (int i = 0; i < matches.size(); i++) {
            System.out.println((i + 1) + ". " + matches.get(i));
        }
    }

    /**
     * Function to inform the user that a task was added and display task count.
     *
     * @param t    the task that was added
     * @param size the current number of tasks in the list
     */
    public void showAdded(Task t, int size) {
        System.out.println("Got it. I've added this task:\n" + t);
        System.out.println("Now you have " + size + " task(s) left in the list");
    }

    /**
     * Function to print all tasks in the task list to the user.
     *
     * @param tasks the task list wrapper containing tasks
     */
    public void showList(TaskList tasks) {
        System.out.println("Here are the tasks in your list:\n");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i));
        }
    }

    /**
     * Function to inform the user that a task was marked as done.
     *
     * @param t the task that was marked
     */
    public void showMarked(Task t) {
        System.out.println("Nice! I've marked this task as done:\n" + t);
    }

    /**
     * Function to inform the user that a task was marked as not done.
     *
     * @param t the task that was unmarked
     */
    public void showUnmarked(Task t) {
        System.out.println("OK, I've marked this task as not done yet:\n" + t);
    }

    /**
     * Function to inform the user that a task was deleted and display task count.
     *
     * @param removed the task that was removed
     * @param size    the current number of tasks in the list
     */
    public void showDeleted(Task removed, int size) {
        System.out.println("Noted. I've removed this task:\n" + removed);
        System.out.println("Now you have " + size + " task(s) in the list.");
    }
}
