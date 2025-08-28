/**
 * Deadlines are tasks which have the special attribute, a string "by" which indicates the date at which this task
 * has to be completed by
 */
public class Deadline extends Task{

    protected String by;

    /**
     * This method creates an instance of a Deadline which uses the engine of Task but also assigns the string "by"
     * upon calling it with a given object
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
