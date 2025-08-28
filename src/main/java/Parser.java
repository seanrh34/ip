import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to parse user commands and transform them into structured actions or new tasks.
 */
public final class Parser {
    private Parser() {}

    // Strict formatter: DD/MM/YYYY HHMM (single-digit day/month allowed)
    public static final DateTimeFormatter DMY_HM = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");

    // Matches "todo <task_name>"
    private static final Pattern TODO_PATTERN =
            Pattern.compile("^todo\\s+(.+)$", Pattern.CASE_INSENSITIVE);

    // Matches "deadline <task_name> /by <time>"
    private static final Pattern DEADLINE_PATTERN =
            Pattern.compile("^deadline\\s+(.+)\\s+/by\\s+(.+)$", Pattern.CASE_INSENSITIVE);

    // Matches "event <desc> /from <start_time> /to <end_time>"
    private static final Pattern EVENT_PATTERN =
            Pattern.compile("^event\\s+(.+)\\s+/from\\s+(.+)\\s+/to\\s+(.+)$", Pattern.CASE_INSENSITIVE);

    /**
     * Function to parse a raw user command string into a Parsed object representing the action.
     * @param input the raw user command
     * @return a structured Parsed instance indicating the action to perform
     * @throws JohnException if the command is invalid or cannot be parsed
     */
    public static Parsed parse(String input) throws JohnException {
        String s = input.trim();

        if (s.equalsIgnoreCase("bye"))  return Parsed.exit();
        if (s.equalsIgnoreCase("list")) return Parsed.list();

        if (s.startsWith("mark") || s.startsWith("unmark") || s.startsWith("delete")) {
            String[] split = s.split("\\s+");
            if (split.length != 2) {
                throw new JohnException("Invalid input! Please provide a single task number.");
            }
            int idx1;
            try {
                idx1 = Integer.parseInt(split[1]);
            } catch (NumberFormatException e) {
                throw new JohnException("Invalid index! Task number must be a whole number.");
            }
            int idx = idx1 - 1;
            if (idx < 0) {
                throw new JohnException("Invalid index! Use a positive number.");
            }
            if (s.startsWith("mark"))   return Parsed.mark(idx);
            if (s.startsWith("unmark")) return Parsed.unmark(idx);
            return Parsed.delete(idx);
        }

        if (s.startsWith("todo")) {
            Matcher m = TODO_PATTERN.matcher(s);
            if (!m.matches()) {
                throw new JohnException("Invalid format for todo. Usage: todo <task_name>");
            }
            String desc = m.group(1).trim();
            if (desc.isEmpty()) {
                throw new JohnException("The description of a todo cannot be empty.");
            }
            return Parsed.add(new ToDo(desc));
        }

        if (s.startsWith("deadline")) {
            Matcher m = DEADLINE_PATTERN.matcher(s);
            if (!m.matches()) {
                throw new JohnException(
                        "Invalid format for deadline. Usage: deadline <desc> /by <DD/MM/YYYY HHMM>");
            }
            String desc = m.group(1).trim();
            String byStr = m.group(2).trim();
            if (desc.isEmpty() || byStr.isEmpty()) {
                throw new JohnException(
                        "A deadline requires <desc> and /by <date time>. " +
                                "Example: deadline return book /by 28/8/2025 1800");
            }
            LocalDateTime by = parseDateStrict(byStr);
            return Parsed.add(new Deadline(desc, by));
        }

        if (s.startsWith("event")) {
            Matcher m = EVENT_PATTERN.matcher(s);
            if (!m.matches()) {
                throw new JohnException(
                        "Invalid format for event. Usage: event <task_name> /from <start> /to <end> (DD/MM/YYYY HHMM)");
            }
            String desc = m.group(1).trim();
            String fromStr = m.group(2).trim();
            String toStr = m.group(3).trim();
            if (desc.isEmpty() || fromStr.isEmpty() || toStr.isEmpty()) {
                throw new JohnException(
                        "An event requires a description, /from time, and /to time. " +
                                "Example: event meeting /from 28/8/2025 1800 /to 28/8/2025 2000");
            }
            LocalDateTime from = parseDateStrict(fromStr);
            LocalDateTime to = parseDateStrict(toStr);
            return Parsed.add(new Event(desc, from, to));
        }

        throw new JohnException("Unknown command.");
    }

    /**
     * Function to strictly parse a date-time string using DD/MM/YYYY HHMM.
     * @param s the date-time string
     * @return a LocalDateTime parsed from the string
     * @throws JohnException if the input string is not in the expected format
     */
    private static LocalDateTime parseDateStrict(String s) throws JohnException {
        try {
            return LocalDateTime.parse(s, DMY_HM);
        } catch (DateTimeParseException e) {
            throw new JohnException("Invalid date/time. Use only DD/MM/YYYY HHMM, e.g. 28/8/2025 1800.");
        }
    }

    /**
     * Class to represent the parsed result of a user command.
     */
    public static final class Parsed {
        public enum Kind { EXIT, LIST, ADD, MARK, UNMARK, DELETE }
        public final Kind kind;
        public final Task task; // for ADD
        public final int index; // for mark/unmark/delete

        /**
         * Function to construct a parsed object.
         * @param k the kind of parsed action
         * @param t the task if any (for ADD)
         * @param i the index if any (for mark/unmark/delete)
         */
        private Parsed(Kind k, Task t, int i) {
            this.kind = k;
            this.task = t;
            this.index = i;
        }

        /**
         * Function to create a parsed object representing program exit.
         * @return a Parsed instance for EXIT
         */
        public static Parsed exit() { return new Parsed(Kind.EXIT, null, -1); }

        /**
         * Function to create a parsed object representing list action.
         * @return a Parsed instance for LIST
         */
        public static Parsed list() { return new Parsed(Kind.LIST, null, -1); }

        /**
         * Function to create a parsed object representing adding a task.
         * @param t the task to add
         * @return a Parsed instance for ADD
         */
        public static Parsed add(Task t) { return new Parsed(Kind.ADD, t, -1); }

        /**
         * Function to create a parsed object representing marking a task.
         * @param idx the zero-based index of the task to mark
         * @return a Parsed instance for MARK
         */
        public static Parsed mark(int idx) { return new Parsed(Kind.MARK, null, idx); }

        /**
         * Function to create a parsed object representing unmarking a task.
         * @param idx the zero-based index of the task to unmark
         * @return a Parsed instance for UNMARK
         */
        public static Parsed unmark(int idx) { return new Parsed(Kind.UNMARK, null, idx); }

        /**
         * Function to create a parsed object representing deleting a task.
         * @param idx the zero-based index of the task to delete
         * @return a Parsed instance for DELETE
         */
        public static Parsed delete(int idx) { return new Parsed(Kind.DELETE, null, idx); }
    }
}