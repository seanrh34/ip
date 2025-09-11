package john;

/**
 * A Task class has the attributes description and isDone, where the former is a string that describes a given task
 * and the latter is a boolean to show whether a task is done or not
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Function to create a new instance of a basic Task
     * @param description
     */
    public Task(String description) {
        assert description != null : "description cannot be null";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Function to get the icon that shows whether a task is done or not
     * @return
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    public String getDesc() {
        return this.description;
    }

    public boolean getIsDone() {
        return this.isDone;
    }

    /**
     * Function to mark a task as done
     */
    public void mark() {
        this.isDone = true;
    }

    /**
     * Function to mark a task as NOT done
     */
    public void unmark() {
        this.isDone = false;
    }

    /**
     * Function to return a string representation of the task in a format compatible with the .txt storage
     * @return
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
