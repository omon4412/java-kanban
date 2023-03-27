package kanban.models;

import java.util.Objects;

public class Task {

    protected int id = -1;
    protected String name;
    protected TaskStatus status;

    public Task(String name) {
        this.name = name;
        this.status = TaskStatus.NEW;
    }

    public Task(String name, TaskStatus status) {
        this.name = name;
        this.status = status;
    }

    public Task(Task other) {
        this(other.name, other.status);
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
        return id == task.id && Objects.equals(name, task.name) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                '}';
    }
}