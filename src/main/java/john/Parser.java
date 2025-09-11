package john;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to parse user commands and transform them into structured actions or new tasks.
 */
public final class Parser {
    // Formatter: DD/MM/YYYY HHMM (single-digit day/month allowed)
    public static final DateTimeFormatter DMY_HM = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");
    // Matches "deadline <task_name> /by <time>"
    private static final Pattern DEADLINE_PATTERN =
            Pattern.compile("^deadline\\s+(.+)\\s+/by\\s+(.+)$", Pattern.CASE_INSENSITIVE);
    // Matches "event <desc> /from <start_time> /to <end_time>"
    private static final Pattern EVENT_PATTERN =
            Pattern.compile("^event\\s+(.+)\\s+/from\\s+(.+)\\s+/to\\s+(.+)$", Pattern.CASE_INSENSITIVE);
    // Matches "find <keyword>"
    private static final Pattern FIND_PATTERN =
            Pattern.compile("^find\\s+(.+)$", Pattern.CASE_INSENSITIVE);
    // Matches "todo <task_name>"
    private static final Pattern TODO_PATTERN =
            Pattern.compile("^todo\\s+(.+)$", Pattern.CASE_INSENSITIVE);

    private Parser() {
    }

    /**
     * Function to parse a raw user command string into a Parsed object representing the action.
     *
     * @param input the raw user command
     * @return a structured Parsed instance indicating the action to perform
     * @throws JohnException if the command is invalid or cannot be parsed
     */
    public static Parsed parse(String input) throws JohnException {
        assert input != null : "Input cannot be null";
        String s = input.trim();
        String[] split = s.split("\\s+", 2); // command + args
        String cmd = split[0];

        return switch (cmd) {
        case "bye" -> Parsed.exit();
        case "list" -> Parsed.list();
        case "help" -> Parsed.help();
        case "find" -> parseFind(s);
        case "mark", "unmark", "delete" -> parseModify(s, cmd);
        case "todo" -> parseTodo(s);
        case "deadline" -> parseDeadline(s);
        case "event" -> parseEvent(s);
        default -> Parsed.unknown();
        };
    }

    /**
     * Function to handle the "find" keyword in the above switch case bracket
     * @param s a string
     * @return Parsed object
     * @throws JohnException if the format is invalid
     */
    private static Parsed parseFind(String s) throws JohnException {
        Matcher m = FIND_PATTERN.matcher(s);
        if (!m.matches()) {
            throw new JohnException("Invalid format for find. Usage: find <keyword>");
        }
        String keyword = m.group(1).trim();
        if (keyword.isEmpty()) {
            throw new JohnException("The keyword for find cannot be empty.");
        }
        return Parsed.find(keyword);
    }
    /**
     * Function to handle "mark", "unmark", and "delete" keywords
     * @param s String s
     * @param cmd String
     * @return Parsed object
     * @throws JohnException if format is invalid or index is invalid
     */
    private static Parsed parseModify(String s, String cmd) throws JohnException {
        String[] parts = s.split("\\s+");
        if (parts.length != 2) {
            throw new JohnException("Invalid input! Please provide a single task number.");
        }
        int idx1;
        try {
            idx1 = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new JohnException("Invalid index! Task number must be a whole number.");
        }
        int idx = idx1 - 1;
        if (idx < 0) {
            throw new JohnException("Invalid index! Use a positive number.");
        }
        return switch (cmd) {
        case "mark" -> Parsed.mark(idx);
        case "unmark" -> Parsed.unmark(idx);
        default -> Parsed.delete(idx);
        };
    }

    /**
     * Function to handle "todo" keyword
     * @param s a string
     * @throws JohnException for invalid formats
     */
    private static Parsed parseTodo(String s) throws JohnException {
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

    /**
     * Function to handle "deadline" keyword
     * @param s a string
     * @return Parsed object
     * @throws JohnException if the format of s is wrong
     */
    private static Parsed parseDeadline(String s) throws JohnException {
        Matcher m = DEADLINE_PATTERN.matcher(s);
        if (!m.matches()) {
            throw new JohnException(
                    "Invalid format for deadline. Usage: deadline <desc> /by <DD/MM/YYYY HHMM>");
        }
        String desc = m.group(1).trim();
        String byStr = m.group(2).trim();
        if (desc.isEmpty() || byStr.isEmpty()) {
            throw new JohnException(
                    "A deadline requires <desc> and /by <date time>. "
                            + "Example: deadline return book /by 28/8/2025 1800");
        }
        LocalDateTime by = parseDateStrict(byStr);
        return Parsed.add(new Deadline(desc, by));
    }

    /**
     * Function to handle "event" keyword
     * @param s a string
     * @return Parsed object
     * @throws JohnException if the format of s is invalid
     */
    private static Parsed parseEvent(String s) throws JohnException {
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
                    "An event requires a description, /from time, and /to time. "
                            + "Example: event meeting /from 28/8/2025 1800 /to 28/8/2025 2000");
        }
        LocalDateTime from = parseDateStrict(fromStr);
        LocalDateTime to = parseDateStrict(toStr);
        return Parsed.add(new Event(desc, from, to));
    }

    /**
     * Function to strictly parse a date-time string using DD/MM/YYYY HHMM.
     *
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
        public final Kind kind;
        public final Task task; // for ADD
        public final int index; // for mark/unmark/delete
        public final String query; // for find

        /**
         * Function to construct a parsed object (non-query actions).
         *
         * @param k the kind of parsed action
         * @param t the task if any (for ADD)
         * @param i the index if any (for mark/unmark/delete)
         */
        private Parsed(Kind k, Task t, int i) {
            this.kind = k;
            this.task = t;
            this.index = i;
            this.query = null;
        }

        /**
         * Function to construct a parsed object for query actions such as find.
         *
         * @param k the kind of parsed action
         * @param q the query string (e.g., keyword for find)
         */
        private Parsed(Kind k, String q) {
            this.kind = k;
            this.task = null;
            this.index = -1;
            this.query = q;
        }

        /**
         * Function to create a parsed object representing and invalid command.
         *
         * @return Parsed
         */
        public static Parsed unknown() {
            return new Parsed(Kind.UNKNOWN, null);
        }

        /**
         * Function to create a parsed object representing providing a list of available commands.
         *
         * @return Parsed
         */
        public static Parsed help() {
            return new Parsed(Kind.HELP, null);
        }

        /**
         * Function to create a parsed object representing program exit.
         */
        public static Parsed exit() {
            return new Parsed(Kind.EXIT, null, -1);
        }

        /**
         * Function to create a parsed object representing list action.
         */
        public static Parsed list() {
            return new Parsed(Kind.LIST, null, -1);
        }

        /**
         * Function to create a parsed object representing adding a task.
         */
        public static Parsed add(Task t) {
            return new Parsed(Kind.ADD, t, -1);
        }

        /**
         * Function to create a parsed object representing marking a task.
         */
        public static Parsed mark(int idx) {
            return new Parsed(Kind.MARK, null, idx);
        }

        /**
         * Function to create a parsed object representing unmarking a task.
         */
        public static Parsed unmark(int idx) {
            return new Parsed(Kind.UNMARK, null, idx);
        }

        /**
         * Function to create a parsed object representing deleting a task.
         */
        public static Parsed delete(int idx) {
            return new Parsed(Kind.DELETE, null, idx);
        }

        /**
         * Function to create a parsed object representing a find action.
         *
         * @param keyword the keyword to search for in task descriptions
         * @return a Parsed instance for FIND
         */
        public static Parsed find(String keyword) {
            return new Parsed(Kind.FIND, keyword);
        }

        /**
         * Enumeration for fixed items to look out for while parsing
         */
        public enum Kind { EXIT, LIST, ADD, MARK, UNMARK, DELETE, FIND, HELP, UNKNOWN }
    }
}
