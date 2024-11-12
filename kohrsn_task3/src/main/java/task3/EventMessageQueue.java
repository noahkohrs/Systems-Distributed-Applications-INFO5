package task3;

/**
 * The {@link EventMessageQueue} is a queue-based messaging system that facilitates asynchronous message passing.
 * <br>
 * It provides methods for sending and receiving byte-encoded messages and managing the queue's lifecycle.
 * Implementations of {@link EventMessageQueue} allow for registering a {@link Listener} to handle incoming messages
 * and queue closure events.
 *
 * @see EventQueueBroker
 */
public abstract class EventMessageQueue {

    /**
     * Listener interface to handle incoming messages and connection lifecycle events.
     */
    public interface Listener {
        /**
         * Called when a new message is received by this {@link EventMessageQueue}.
         * <br>
         * The function will never be executed concurrently with other events.
         *
         * @param msg the received message
         */
        void received(EventMessage msg);

        /**
         * Called when the {@link EventMessageQueue} has been closed.
         * <br>
         * The function will never be executed concurrently with other events.
         */
        void closed();
    }

    /**
     * Registers a {@link Listener} to receive notifications for incoming messages and closure events.
     * <br>
     * Only one listener can be registered at a time, subsequent calls to this method will replace the existing listener.
     *
     * @param l the listener to be registered
     */
    public abstract void setListener(Listener l);

    /**
     * Sends a byte array message through this {@link EventMessageQueue}.
     *
     * @param msg the byte array containing the message to be sent
     * @return {@code true} if the message was successfully sent, {@code false} otherwise
     */
    public abstract boolean send(EventMessage msg);

    /**
     * Closes the {@link EventMessageQueue}, preventing any further message transmission or reception.
     * <br>
     * Once closed, the {@link Listener#closed()} method will be invoked if a listener is registered.
     */
    public abstract void close();

    /**
     * Checks whether this {@link EventMessageQueue} has been closed.
     *
     * @return {@code true} if the queue is closed, {@code false} otherwise
     */
    public abstract boolean closed();
}
