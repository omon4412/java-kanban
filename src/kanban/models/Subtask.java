package kanban.models;

import java.util.Objects;

public class Subtask extends Task {

    protected int epicId = -1;

    public Subtask(String name) {
        super(name);
    }

    public Subtask(String name, TaskStatus status) {
        super(name, status);
    }

    public Subtask(Subtask other) {
        this(other.name, other.status);
        this.epicId = other.epicId;
        this.id = other.id;
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
                ", description=" + description +
                '}';
    }

    @Override
    public String toCsvString() {
        return id + "," + TaskType.SUBTASK + "," + name + "," + status + "," + description + "," + epicId;
    }

    @Override
    public void fromScsString(String csvString) {
        String[] data = csvString.split(",", 6);
        this.id = Integer.parseInt(data[0]);
        this.name = data[2];
        this.status = TaskStatus.valueOf(data[3]);
        this.description = data[4];
        this.epicId = Integer.parseInt(data[5]);
    }
}
