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
        boolean exit = false;

        while (sc.hasNextLine() && !exit) {
            String input = sc.nextLine();
            if (input.equals("bye")) {
                System.out.println(exitMsg);
                exit = true;
            } else {
                System.out.println(LINE + input + '\n' + LINE);
            }
        }
    }
}
