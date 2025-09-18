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
     * Constructs a new Deadline task with the specified description and due date.
     * @param description The description of the deadline task.
     * @param by The date and time by which this task must be completed.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        assert by != null : "by cannot be null and must be a valid LocalDateTime";
        this.by = by;
    }

    /**
     * Returns the date and time by which this task must be completed.
     * @return The deadline date and time.
     */
    public LocalDateTime getBy() {
        return this.by;
    }

    @Override
    public String toFileFormatString() {
        String byString = by.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"));
        return "D | " + (isDone ? "Done" : "Not Done") + " | " + description + " | By: " + byString;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + this.by.format(DISPLAY) + ")";
    }
}
