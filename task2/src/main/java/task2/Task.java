package task2;

import task1.Broker;

public abstract class Task extends task1.Task {

    private final QueueBroker queueBroker;

    /**
     * Create a new task associated with the given broker.
     *
     * @param broker the broker that this task is associated with.
     * @param task   the task to execute.
     */
    public Task(Broker broker, Runnable task) {
        super(broker, task);
        this.queueBroker = null;
    }

    /**
     * Create a new task associated with the given queue broker.
     *
     * @param queueBroker the broker that this task is associated with.
     * @param task        the task to execute.
     */
    public Task(QueueBroker queueBroker, Runnable task) {
        super(queueBroker.broker, task);
        this.queueBroker = queueBroker;
    }

    /**
     * Get the queue broker associated with this task.
     *
     * @return the queue broker associated with this task.
     */
    public static QueueBroker getQueueBroker() {
        var currentTask = Thread.currentThread();
        if (currentTask instanceof Task) {
            return ((Task) currentTask).queueBroker;
        }
        throw new IllegalStateException("Not a task thread");
    }
}
