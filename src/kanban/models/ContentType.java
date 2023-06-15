package kanban.models;

public enum ContentType {
    JSON("application/json; charset=utf-8"),
    TEXT("text/html; charset=UTF-8");
    public final String label;

    ContentType(String s) {
        this.label = s;
    }
}
