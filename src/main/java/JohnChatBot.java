import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JohnChatBot{
    // Fixed Messages
    private static final String LINE = "=================================================\n";
    private static final String greetingMsg = LINE
            + "Hello! I'm JohnChatBot.\n"
            + "What can I do for you?\n"
            + LINE;

    private static final String exitMsg = LINE
            + "Bye. Hope to see you again soon!\n"
            + LINE;


    // For regex
    // Matches "todo <desc>"
    private static final Pattern TODO_PATTERN =
            Pattern.compile("^todo\\s+(.+)$", Pattern.CASE_INSENSITIVE);

    // Matches "deadline <desc> /by <deadline>"
    private static final Pattern DEADLINE_PATTERN =
            Pattern.compile("^deadline\\s+(.+)\\s+/by\\s+(.+)$", Pattern.CASE_INSENSITIVE);

    // Matches "event <desc> /from <start> /to <end>"
    private static final Pattern EVENT_PATTERN =
            Pattern.compile("^event\\s+(.+)\\s+/from\\s+(.+)\\s+/to\\s+(.+)$", Pattern.CASE_INSENSITIVE);

    public static void main(String[] args) {
        System.out.println(greetingMsg);

        Scanner sc = new Scanner(System.in);


        // Variables
        boolean exit = false;
        List<Task> task_list = new ArrayList<>();

        while (sc.hasNextLine() && !exit) {
            String input = sc.nextLine();

            try {
                if (input.equals("bye")) {
                    System.out.println(exitMsg);
                    exit = true;
                } else if (input.equals("list")) {
                    System.out.print(LINE);
                    System.out.println("Here are the tasks in your list:\n");
                    for (int i = 0; i < task_list.size(); i++) {
                        Task curTask = task_list.get(i);
                        System.out.println((i+1)  + ". " + curTask.toString());
                    }
                    System.out.print(LINE);
                } else if (input.startsWith("mark") || input.startsWith("unmark")) {
                    String[] splitInput = input.split(" ");
                    if (splitInput.length == 2) {
                        try {
                            int index = Integer.parseInt(splitInput[1]) - 1;
                            if (index < 0 || index >= task_list.size()) {
                                System.out.println("Invalid index! Please enter a number between 1 and "
                                        + task_list.size());
                                continue;
                            }

                            Task curTask = task_list.get(index);

                            if (splitInput[0].equals("mark")) {
                                curTask.mark();
                                System.out.println("Nice! I've marked this task as done:\n"
                                        + curTask);
                            } else if (splitInput[0].equals("unmark")) {
                                curTask.unmark();
                                System.out.println("OK, I've marked this task as not done yet:\n"
                                        + curTask);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input! Please enter a number between 1 and "
                                    + task_list.size());
                        }
                    }
                } else if (input.startsWith("todo") || input.startsWith("deadline") || input.startsWith("event")) {
                    Matcher m;

                    if (input.startsWith("todo")) {
                        m = TODO_PATTERN.matcher(input);
                        if (m.matches()) {
                            String desc = m.group(1).trim();
                            if (desc.isEmpty()) {
                                throw new JohnException("The description of a todo cannot be empty.");
                            }
                            task_list.add(new ToDo(desc));
                        } else {
                            throw new JohnException("Invalid format for todo. Usage: todo <task_name>");
                        }

                    } else if (input.startsWith("deadline")) {
                        m = DEADLINE_PATTERN.matcher(input);
                        if (m.matches()) {
                            String desc = m.group(1).trim();
                            String by = m.group(2).trim();
                            if (desc.isEmpty() || by.isEmpty()) {
                                throw new JohnException("A deadline requires both a description and a /by time.");
                            }
                            task_list.add(new Deadline(desc, by));
                        } else {
                            throw new JohnException("Invalid format for deadline. " +
                                    "Usage: deadline <task_name> /by <time>");
                        }

                    } else if (input.startsWith("event")) {
                        m = EVENT_PATTERN.matcher(input);
                        if (m.matches()) {
                            String desc = m.group(1).trim();
                            String from = m.group(2).trim();
                            String to = m.group(3).trim();
                            if (desc.isEmpty() || from.isEmpty() || to.isEmpty()) {
                                throw new JohnException("An event requires a description, /from time, and /to time.");
                            }
                            task_list.add(new Event(desc, from, to));
                        } else {
                            throw new JohnException("Oops! Invalid format for event. " +
                                    "Usage: event <task_name> /from <start_time> /to <end_time>");
                        }
                    }

                    Task new_task = task_list.get(task_list.size()-1);
                    System.out.print(LINE);
                    System.out.println("Got it. I've added this task:\n"
                            + new_task + "\n"
                            + "Now you have " + task_list.size() + " task(s) left in the list");
                    System.out.print(LINE);
                } else {
                    throw new JohnException("This command is not recognised, here is the list of valid inputs:\n"
                            + "1. bye - Exit the chatbot\n"
                            + "2. list - List all current tasks\n"
                            + "3. mark <task_number> - Mark the task that corresponds to task_number from the" +
                            " \"list\" command as done.\n"
                            + "4. unmark <task_number> - Unmark the task that corresponds to task_number from the" +
                            " \"list\" command as undone.\n"
                            + "5. todo <task_name> - Add a new todo task with no deadlines or duration" +
                            "with the name <task_name>\n"
                            + "6. deadline <task_name> /by <time> - Add a new deadline task with a deadlines" +
                            "with the name <task_name> and a deadline by <time>\n"
                            + "7. event <task_name> /from <start_time> /to <end_time> " +
                            "- Add a new todo task with a duration" +
                            "with the name <task_name> from <start_time> to <end_time>\n");
                }
            } catch (JohnException e) {
                System.out.print(LINE);
                System.out.println(e.getMessage());
                System.out.print(LINE);
            }
        }
    }
}
