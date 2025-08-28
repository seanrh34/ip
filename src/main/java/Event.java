import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 * Events are Tasks which have the special attribute of strings "to" and "from" to show the duration of an event.
 * It is recognised with the letter 'E' in displays
 */
public class Event extends Task{
    private final LocalDateTime from;
    private final LocalDateTime to;
    private static final DateTimeFormatter DISPLAY = DateTimeFormatter.ofPattern("MMM dd yyyy");

    /**
     * This method creates a new instance of an Event which uses the constructor of Task but also assigns the
     * attributes from and to into the instance
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    public LocalDateTime getFrom() {
        return this.from;
    }

    public LocalDateTime getTo() {
        return this.to;
    }

    @Override
    public String toFileFormatString() {
        String from_string = this.from.format(DISPLAY);
        String to_string = this.to.format(DISPLAY);
        return "E | " + super.toFileFormatString() + " | From: " + from_string + " | To: " + to_string;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(DISPLAY) + " to: " + to.format(DISPLAY) + ")";
    }
}
