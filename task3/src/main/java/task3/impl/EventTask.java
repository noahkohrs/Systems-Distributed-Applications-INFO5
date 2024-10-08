package task3.impl;

/**
 * This is just made to improve the debugging by providing a name.
 */
public class EventTask {
    String name;
    Runnable runnable;

    public EventTask(String name, Runnable runnable) {
        this.name = name;
        this.runnable = runnable;
    }

    public void run() {
        runnable.run();
    }
}
