package john.tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Events are Tasks which have the special attribute of strings "to" and "from" to show the duration of an event.
 * It is recognised with the letter 'E' in displays
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY = DateTimeFormatter.ofPattern("MMM dd yyyy");
    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Constructs a new Event task with the specified description and time range.
     * 
     * @param description The description of the event.
     * @param from The start date and time of the event.
     * @param to The end date and time of the event.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        assert from != null : "from cannot be null and must be a valid LocalDateTime";
        this.from = from;
        assert to != null : "to cannot be null and must be a valid LocalDateTime";
        this.to = to;
    }

    /**
     * Returns the start date and time of this event.
     * 
     * @return The start date and time.
     */
    public LocalDateTime getFrom() {
        return this.from;
    }

    /**
     * Returns the end date and time of this event.
     * 
     * @return The end date and time.
     */
    public LocalDateTime getTo() {
        return this.to;
    }

    @Override
    public String toFileFormatString() {
        String fromString = this.from.format(DISPLAY);
        String toString = this.to.format(DISPLAY);
        return "E | " + super.toFileFormatString() + " | From: " + fromString + " | To: " + toString;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(DISPLAY) + " to: " + to.format(DISPLAY) + ")";
    }
}
