package john.tasks;

/**
 * A Task class has the attributes description and isDone, where the former is a string that describes a given task
 * and the latter is a boolean to show whether a task is done or not
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Constructs a new Task with the specified description.
     * The task is initially marked as not done.
     * 
     * @param description The description of the task.
     */
    public Task(String description) {
        assert description != null : "description cannot be null";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the icon that shows whether a task is done or not.
     * 
     * @return "X" if the task is done, " " (space) if not done.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /**
     * Returns the description of this task.
     * 
     * @return The task description.
     */
    public String getDesc() {
        return this.description;
    }

    /**
     * Returns whether this task is marked as done.
     * 
     * @return True if the task is done, false otherwise.
     */
    public boolean getIsDone() {
        return this.isDone;
    }

    /**
     * Marks this task as done.
     */
    public void mark() {
        this.isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void unmark() {
        this.isDone = false;
    }

    /**
     * Returns a string representation of the task in a format compatible with file storage.
     * 
     * @return A formatted string containing the task's done status and description.
     */
    public String toFileFormatString() {
        String doneStr = "";

        if (this.isDone) {
            doneStr = "Done";
        } else {
            doneStr = "Not Done";
        }

        return doneStr + " | " + description;
    }

    @Override
    public String toString() {
        return "[" + this.getStatusIcon() + "] " + this.description;
    }
}
