/**
 * A Task class has the attributes description and isDone, where the former is a string that describes a given task
 * and the latter is a boolean to show whether a task is done or not
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Function to create a new Task object
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Function to get the icon that shows whether a task is done or not
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

   public String getDesc() {
        return this.description;
   }

   public void setDesc(String newDesc) {
        this.description = newDesc;
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

   @Override
    public String toString(){
        return "[" + this.getStatusIcon() + "] " + this.description;
   }
}
