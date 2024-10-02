package task3;

/**
 * The {@link MessageQueue} is a queue-based messaging system that facilitates asynchronous message passing.
 * <br>
 * It provides methods for sending and receiving byte-encoded messages and managing the queue's lifecycle.
 * Implementations of {@link MessageQueue} allow for registering a {@link Listener} to handle incoming messages
 * and queue closure events.
 * <br>
 * A {@link MessageQueue} is thread-safe and supports concurrent sending and receiving of messages.
 *
 * @see QueueBroker
 */
public abstract class MessageQueue {

    /**
     * Listener interface to handle incoming messages and connection lifecycle events.
     */
    public interface Listener {
        /**
         * Called when a new message is received by this {@link MessageQueue}.
         *
         * @param msg the byte array containing the received message
         */
        void received(byte[] msg);

        /**
         * Called when the {@link MessageQueue} has been closed.
         */
        void closed();
    }

    /**
     * Registers a {@link Listener} to receive notifications for incoming messages and closure events.
     *
     * @param l the listener to be registered
     */
    public abstract void setListener(Listener l);

    /**
     * Sends a byte array message through this {@link MessageQueue}.
     *
     * @param bytes the byte array containing the message to be sent
     * @return {@code true} if the message was successfully sent, {@code false} otherwise
     */
    public abstract boolean send(byte[] bytes);

    /**
     * Sends a portion of a byte array message through this {@link MessageQueue}.
     * <br>
     * The message is sent starting from the given offset and up to the specified length.
     *
     * @param bytes  the byte array containing the message to be sent
     * @param offset the starting position in the byte array
     * @param length the number of bytes to send from the offset
     * @return {@code true} if the message was successfully sent, {@code false} otherwise
     */
    public abstract boolean send(byte[] bytes, int offset, int length);

    /**
     * Closes the {@link MessageQueue}, preventing any further message transmission or reception.
     * <br>
     * Once closed, the {@link Listener#closed()} method will be invoked if a listener is registered.
     */
    public abstract void close();

    /**
     * Checks whether this {@link MessageQueue} has been closed.
     *
     * @return {@code true} if the queue is closed, {@code false} otherwise
     */
    public abstract boolean closed();
}
