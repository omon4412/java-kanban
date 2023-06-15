package kanban.models;

import java.time.Instant;
import java.util.Objects;

public class Task implements CsvConvertable {

    private static final long MILLIS_IN_SECOND = 1_000;
    private static final long SECOND_IN_MINUTES = 60;

    protected int id = -1;
    protected String name;
    protected TaskStatus status;
    protected String description;
    protected long duration;

    protected Instant startTime;

    public Task() {
        this.name = "";
        this.description = "";
        this.status = TaskStatus.NEW;
    }

    public Task(String name) {
        this.name = name;
        this.status = TaskStatus.NEW;
        this.description = "";
    }

    public Task(String name, Instant startTime, long duration) {
        this.name = name;
        this.status = TaskStatus.NEW;
        this.description = "";
        this.startTime = startTime;
        this.duration = duration;
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

    public Task(String name, String description, Instant startTime, long duration) {
        this(name, description);
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, String description, TaskStatus status, Instant startTime, long duration) {
        this(name, description, status);
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(Task other) {
        this(other.name, other.description, other.status, other.startTime, other.duration);
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
        return id == task.id && duration == task.duration
                && Objects.equals(name, task.name) && status == task.status
                && Objects.equals(description, task.description) && Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, status, description, duration, startTime);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", startTime='" + startTime + '\'' +
                ", duration=" + duration +
                '}';
    }

    @Override
    public String toCsvString() {
        return String.join(",", Integer.toString(id), TaskType.TASK.toString(),
                name, status.toString(), description,
                (startTime == null ? "null" : Long.toString(startTime.toEpochMilli())), Long.toString(duration));
    }

    @Override
    public void fromScsString(String csvString) {
        String[] data = csvString.split(",", 7);
        this.id = Integer.parseInt(data[0]);
        this.name = data[2];
        this.status = TaskStatus.valueOf(data[3]);
        this.description = data[4];
        this.startTime = Objects.equals(data[5], "null") ? null : Instant.ofEpochMilli(Long.parseLong(data[5]));
        this.duration = Long.parseLong(data[6]);
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDuration() {
        return duration;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        if (startTime == null) {
            return null;
        } else {
            return startTime.plusMillis(duration * SECOND_IN_MINUTES * MILLIS_IN_SECOND);
        }
    }
}
