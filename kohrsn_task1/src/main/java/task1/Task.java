package task1;

/**
 * A task is a thread that can be executed by a broker.
 *
 * @see Broker
 */
public class Task extends Thread {
    private final Broker broker;

    /**
     * Create a new task associated with the given broker.
     *
     * @param broker the broker that this task is associated with.
     * @param task the task to execute.
     */
    public Task(Broker broker, Runnable task) {
        super(task);
        this.broker = broker;
    }

    /**
     * Get the broker that this task is associated with.
     *
     * @return the broker that this task is associated with if the current thread is a task.
     * @throws IllegalStateException if the current thread is not a task.
     */
    public static Broker getBroker() throws IllegalStateException {
        var currentThread = Thread.currentThread();
        if (currentThread instanceof Task) {
            return ((Task) currentThread).broker;
        }
        throw new IllegalStateException("The current thread is not a task");
    }
}
