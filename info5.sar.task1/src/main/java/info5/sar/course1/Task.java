package info5.sar.course1;

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
     * throws ClassCastException if the current thread is not a Task.
     *
     * @return the broker.
     */
    public static Broker getBroker() {
        try {
            return ((Task) Thread.currentThread()).broker;
        } catch (ClassCastException e) {
            throw new IllegalStateException("Cannot get the broker from the current thread as it's not a Task");
        }
    }
}