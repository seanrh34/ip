package john.tasks;

/**
 * ToDos are tasks which do not hany special attributes, recognised with the letter 'T' in displays.
 */
public class ToDo extends Task {

    /**
     * This method creates a new instance of ToDo which uses the constructor method from Task
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
