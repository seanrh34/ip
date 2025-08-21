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
        String[] text_list = new String[100];
        int text_count = 0;

        while (sc.hasNextLine() && !exit) {
            String input = sc.nextLine();
            if (input.equals("bye")) {
                System.out.println(exitMsg);
                exit = true;
            } else if (input.equals("list")) {
                System.out.println(LINE);
                for (int i = 0; i < text_count; i++) {
                    System.out.println((i+1)  + ". " + text_list[i]);
                }
                System.out.println(LINE);
            } else {
                text_list[text_count] = input;
                text_count++;
                System.out.println(LINE + "added: " + input + '\n' + LINE);
            }
        }
    }
}
