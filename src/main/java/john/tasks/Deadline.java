package john.tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Deadlines are tasks which have the special attribute, a string "by" which indicates the date at which this task
 * has to be completed by
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY = DateTimeFormatter.ofPattern("MMM dd yyyy");
    private final LocalDateTime by;

    /**
     * This method creates an instance of a Deadline which uses the engine of Task but also assigns the string "by"
     * upon calling it with a given object
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        assert by != null : "by cannot be null and must be a valid LocalDateTime";
        this.by = by;
    }

    public LocalDateTime getBy() {
        return this.by;
    }

    @Override
    public String toFileFormatString() {
        String by_string = by.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"));
        return "D | " + (isDone ? "Done" : "Not Done") + " | " + description + " | By: " + by_string;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + this.by.format(DISPLAY) + ")";
    }
}
