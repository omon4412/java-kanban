package kanban.models;

import java.util.Objects;

public class Task implements CsvConvertable{

    protected int id = -1;
    protected String name;
    protected TaskStatus status;
    protected String description;

    public Task() {
        this.description = "";
    }

    public Task(String name) {
        this.name = name;
        this.status = TaskStatus.NEW;
        this.description = "";
    }

    public Task(String name, TaskStatus status) {
        this.name = name;
        this.description = "";
        this.status = status;
    }

    public Task(String name, String description, TaskStatus status) {
        this(name, status);
        this.description = description;
    }

    public Task(String name, String description) {
        this(name);
        this.description = description;
    }

    public Task(Task other) {
        this(other.name, other.description, other.status);
        this.id = other.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && status == task.status
                && Objects.equals(description, task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, status, description);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public String toCsvString() {
        return id + "," + TaskType.TASK + "," + name + "," + status + "," + description;
    }

    @Override
    public void fromScsString(String csvString) {
        String[] data = csvString.split(",", 5);
        this.id = Integer.parseInt(data[0]);
        this.name = data[2];
        this.status = TaskStatus.valueOf(data[3]);
        this.description = data[4];
    }
}
