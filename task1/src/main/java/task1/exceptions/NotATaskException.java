package task1.exceptions;

/**
 * Exception thrown when trying to get the broker of a thread that is not a task.
 */
public class NotATaskException extends RuntimeException {
    public NotATaskException(Thread thread) {
        super("The thread " + thread.getName() + " is not a task.");
    }
}
