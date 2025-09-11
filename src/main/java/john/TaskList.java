package john;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class to wrap and manage the list of tasks, providing operations to mutate and access tasks.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Function to construct an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Function to construct a task list from an existing list of tasks.
     * @param tasks the initial list of tasks to load into the task list
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "tasks cannot be null";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Function to return the current number of tasks in the list.
     * @return number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Function to get a task by index (0-based).
     * @param index zero-based index of the task
     * @return the task at the index
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Function to add a task to the list.
     * @param t the task to add
     */
    public void add(Task t) {
        tasks.add(t);
    }

    /**
     * Function to remove and return a task by index (0-based).
     * @param index zero-based index to remove
     * @return the removed task
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Function to mark a task as done by index (0-based).
     * @param index zero-based index to mark
     * @return the marked task
     */
    public Task mark(int index) {
        Task t = tasks.get(index);
        t.mark();
        return t;
    }

    /**
     * Function to unmark a task as not done by index (0-based).
     * @param index zero-based index to unmark
     * @return the unmarked task
     */
    public Task unmark(int index) {
        Task t = tasks.get(index);
        t.unmark();
        return t;
    }

    /**
     * Function to find tasks whose descriptions contain the given keyword (case-insensitive).
     * @param keyword the keyword to look for
     * @return a new list containing matching tasks in their current order
     */
    public List<Task> find(String keyword) {
        assert keyword != null : "keyword cannot be null";
        String needle = keyword.toLowerCase();
        return tasks.stream()
                .filter(t -> t.getDesc().toLowerCase().contains(needle))
                .collect(Collectors.toList());
    }

    /**
     * Function to return a copy of the internal list for persistence.
     * @return a new list containing all current tasks
     */
    public List<Task> asList() {
        return new ArrayList<>(tasks);
    }

    /**
     * Function to return a user-friendly string of all tasks in the list,
     * each prefixed with its 1-based index.
     * Example:
     *   1. [T][ ] read book
     *   2. [D][X] return book (by: Aug 6 2023)
     *
     * @return formatted string of tasks for display
     */
    public String toDisplayString() {
        if (tasks.isEmpty()) {
            return "No tasks in your list.";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            sb.append(i + 1).append(". ").append(tasks.get(i));
            if (i < tasks.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
