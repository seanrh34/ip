import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

    public static void main(String[] args) {
        System.out.println(greetingMsg);

        Scanner sc = new Scanner(System.in);


        // Variables
        boolean exit = false;
        List<Task> task_list = new ArrayList<>();
        int task_count = 0;

        while (sc.hasNextLine() && !exit) {
            String input = sc.nextLine();

            if (input.equals("bye")) {
                System.out.println(exitMsg);
                exit = true;
            } else if (input.equals("list")) {
                System.out.println(LINE);
                System.out.println("Here are the tasks in your list:\n");
                for (int i = 0; i < task_count; i++) {
                    Task curTask = task_list.get(i);
                    System.out.println((i+1)  + ". " + curTask.toString());
                }
                System.out.println(LINE);
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
            } else {
                task_list.add(new Task(input));
                task_count++;
                System.out.println(LINE + "added: " + input + '\n' + LINE);
            }
        }
    }
}
