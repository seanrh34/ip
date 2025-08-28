/**
 * Events are Tasks which have the special attribute of strings "to" and "from" to show the duration of an event.
 * It is recognised with the letter 'E' in displays
 */
public class Event extends Task{

    protected String from;
    protected String to;

    /**
     * This method creates a new instance of an Event which uses the constructor of Task but also assigns the
     * attributes from and to into the instance
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toFileFormatString() {
        return "E | " + super.toFileFormatString() + " | From: " + this.from + " | To: " + this.to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
