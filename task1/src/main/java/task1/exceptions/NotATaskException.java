package task1.exceptions;

public class NotATaskException extends Exception{
    public NotATaskException(Thread thread) {
        super("The thread " + thread.getName() + " is not a task.");
    }
}
