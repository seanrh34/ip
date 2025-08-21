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
                        System.out.println("Invalid input! Please enter a number between 1 and " + task_list.size());
                    }
                }
            } else if (input.startsWith("todo") || input.startsWith("deadline") || input.startsWith("event")) {
                Matcher m;

                if (input.startsWith("todo")) {
                    m = TODO_PATTERN.matcher(input);
                    if (m.matches()) {
                        String desc = m.group(1).trim();
                        task_list.add(new ToDo(desc));
                    }
                } else if (input.startsWith("deadline")) {
                    m = DEADLINE_PATTERN.matcher(input);
                    if (m.matches()) {
                        String desc = m.group(1).trim();
                        String by = m.group(2).trim();
                        task_list.add(new Deadline(desc, by));
                    }
                } else if (input.startsWith("event")) {
                    m = EVENT_PATTERN.matcher(input);
                    if (m.matches()) {
                        String desc = m.group(1).trim();
                        String from = m.group(2).trim();
                        String to = m.group(3).trim();
                        task_list.add(new Event(desc, from, to));
                    }
                }

                Task new_task = task_list.get(task_list.size()-1);
                System.out.print(LINE);
                System.out.println("Got it. I've added this task:\n"
                                + new_task + "\n"
                                + "Now you have " + task_list.size() + " task(s) left in the list");
                System.out.print(LINE);
            } else {
                task_list.add(new Task(input));
                System.out.println(LINE + "added: " + input + '\n' + LINE);
            }
        }
    }
}
