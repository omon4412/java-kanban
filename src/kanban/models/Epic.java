package kanban.models;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private ArrayList<Integer> subtasksIDs = new ArrayList<>();

    protected Instant endTime;

    public Epic() {
        super();
    }

    public Epic(String name) {
        super(name);
        this.description = "";
    }

    public Epic(Epic other) {
        this(other.name, other.description);
        this.subtasksIDs = new ArrayList<>(other.subtasksIDs);
        this.id = other.id;
    }

    public Epic(String name, String description) {
        super(name);
        this.description = description;
    }

    public ArrayList<Integer> getSubtasks() {
        return subtasksIDs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(description, epic.description) && Objects.equals(subtasksIDs, epic.subtasksIDs)
                && Objects.equals(startTime, epic.startTime) && duration == epic.duration;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasksIDs, startTime, duration);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", subtasksIDs=" + subtasksIDs +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", startTime='" + startTime + '\'' +
                ", duration=" + duration +
                '}';
    }

    @Override
    public String toCsvString() {
        return String.join(",", Integer.toString(id), TaskType.EPIC.toString(),
                name, status.toString(), description, (startTime == null ? "null" : startTime.toString()),
                Long.toString(duration));
    }

    @Override
    public void fromScsString(String csvString) {
        super.fromScsString(csvString);
    }

    @Override
    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }
}
