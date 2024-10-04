package task3.impl;

/**
 * An {@link EventTask} is a task abstraction to transfer code execution to the event pump.
 */
abstract class EventTask extends Thread{

    /**
     * Post a {@link Runnable} to the event pump.
     * @param r the runnable to post, should be member of a Listener class
     */
    public abstract void post(Runnable r);

    /**
     * @return the current task instance, or null if not in a task
     */
    public static EventTask task() {
        if (currentThread() instanceof EventTask) {
            return (EventTask) currentThread();
        } else {
            return null;
        }
    }

    /**
     * Kill the task.
     */
    public abstract void kill();

    /**
     * Check if the task is killed.
     * @return true if the task is killed, false otherwise
     */
    public abstract boolean killed();
}
