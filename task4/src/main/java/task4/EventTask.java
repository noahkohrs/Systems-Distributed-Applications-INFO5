package task4;

/**
 * This is just made to improve the debugging by providing a name.
 */
public class EventTask {
    String name;
    Runnable runnable;

    public EventTask(Runnable runnable) {
        this.name = "Unnamed";
        this.runnable = runnable;
    }

    public EventTask(String name, Runnable runnable) {
        this.name = name;
        this.runnable = runnable;
    }

    public void run() {
        runnable.run();
    }
}
