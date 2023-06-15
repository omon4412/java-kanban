package kanban.manager;

import java.io.File;

public class Managers {

    private Managers(){}

    public static TaskManager getDefaultManager(){
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }

    public static TaskManager getFileManager(){
        return FileBackedTasksManager.loadFromFile(new File("resources/data_at_server.csv"));
    }

    public static HttpTaskManager getHttpTaskManager(){
        return new HttpTaskManager("http://localhost:8078");
    }
}
