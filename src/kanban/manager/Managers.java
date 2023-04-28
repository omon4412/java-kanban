package kanban.manager;

public class Managers {

    private Managers(){}

    public static TaskManager getDefaultManager(){
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }
}
