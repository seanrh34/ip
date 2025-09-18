package john.tasks;

/**
 * Represents a basic todo task without any special attributes.
 * ToDos are recognised with the letter 'T' in displays.
 */
public class ToDo extends Task {

    /**
     * Constructs a new ToDo task with the specified description.
     * 
     * @param description The description of the todo task.
     */
    public ToDo(String description) {
        super(description);
    }

    @Override
    public String toFileFormatString() {
        return "T" + " | " + super.toFileFormatString();
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
