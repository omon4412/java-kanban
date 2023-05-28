package kanban.models;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {

    protected int epicId = -1;

    public Subtask() {
        super();
    }

    public Subtask(String name) {
        super(name);
    }

    public Subtask(String name, Instant startTime, long duration) {
        super(name);
        this.startTime = startTime;
        this.duration = duration;

    }

    public Subtask(String name, TaskStatus status) {
        super(name, status);
    }

    public Subtask(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public Subtask(String name, String description, TaskStatus status, Instant startTime, long duration) {
        super(name, description, status);
        this.startTime = startTime;
        this.duration = duration;
    }

    public Subtask(Subtask other) {
        this(other.name, other.status);
        this.epicId = other.epicId;
        this.id = other.id;
        this.description = other.description;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + id +
                ", epicId=" + epicId +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", startTime='" + startTime + '\'' +
                ", duration=" + duration +
                '}';
    }

    @Override
    public String toCsvString() {
        return String.join(",", Integer.toString(id), TaskType.SUBTASK.toString(),
                name, status.toString(), description, (startTime == null ? "null" : startTime.toString()),
                Long.toString(duration), Integer.toString(epicId));
    }

    @Override
    public void fromScsString(String csvString) {
        String[] data = csvString.split(",", 8);
        this.id = Integer.parseInt(data[0]);
        this.name = data[2];
        this.status = TaskStatus.valueOf(data[3]);
        this.description = data[4];
        this.startTime = Objects.equals(data[5], "null") ? null : Instant.parse(data[5]);
        this.duration = Long.parseLong(data[6]);
        this.epicId = Integer.parseInt(data[7].trim());
    }
}
