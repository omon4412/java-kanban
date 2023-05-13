package kanban.models;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private ArrayList<Integer> subtasksIDs = new ArrayList<>();

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
        return Objects.equals(description, epic.description) && Objects.equals(subtasksIDs, epic.subtasksIDs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), description, subtasksIDs);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", subtasksIDs=" + subtasksIDs +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public String toCsvString() {
        return id + "," + TaskType.EPIC + "," + name + "," + status + "," + description;
    }

    @Override
    public void fromScsString(String csvString) {
        super.fromScsString(csvString);
    }
}
