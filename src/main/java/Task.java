public class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

   public String getDesc() {
        return this.description;
   }

   public void setDesc(String newDesc) {
        this.description = newDesc;
   }

   public void mark() {
        this.isDone = true;
   }

   public void unmark() {
        this.isDone = false;
   }

   @Override
    public String toString(){
        return "[" + this.getStatusIcon() + "] " + this.description;
   }
}
