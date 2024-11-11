package task5;

import task4.Broker;

/**
 * A QueueBroker is a high-level abstraction that manages message-based communication
 * by accepting connections and routing {@link Message} objects between hosts.
 * <br>
 * Unlike a {@link task4.Broker}, which works at the byte level, QueueBroker handles entire messages.
 */
public abstract class QueueBroker {

    /**
     * The name of the broker.
     */
    public final String name;

    /**
     * The parent broker that created this broker.
     */
    protected final Broker parentBroker;

    /**
     * Create a new QueueBroker with the given name.
     * @param broker the Broker it derives from.
     */
    public QueueBroker(Broker broker) {
        this.parentBroker = broker;
        this.name = broker.name;
    }

    /**
     * Binds to a given port and starts listening for incoming connections.
     * <br>
     * The method will return immediately and call the {@link AcceptListener#accepted(MessageQueue)} when a connection is established.
     *
     * @param port the port to bind and listen for incoming connections.
     * @param listener the listener to handle newly accepted connections.
     * @throws IllegalStateException if the port is already in use or cannot be bound.
     */
    public abstract void bind(int port, AcceptListener listener);

    /**
     * Stops listening on the given port and unbinds any connections associated with it.
     *
     * @param port the port to unbind.
     */
    public abstract void unbind(int port);

    /**
     * Connects to a remote host on the given port and establishes a message queue.
     * <br>
     * This method is non-blocking and will return immediately, triggering the {@link ConnectListener#connected(MessageQueue)} upon success.
     *
     * @param host the remote host to connect to.
     * @param port the port to connect to.
     * @param listener the listener to handle the connection event.
     */
    public abstract void connect(String host, int port, ConnectListener listener);

    /**
     * Listener interface to handle incoming accepted connections.
     */
    public interface AcceptListener {
        /**
         * Called when a new connection has been accepted and a {@link MessageQueue} is ready for use.
         *
         * @param queue the {@link MessageQueue} created for the accepted connection.
         */
        void accepted(MessageQueue queue);
    }

    /**
     * Listener interface to handle connection results.
     */
    public interface ConnectListener {
        /**
         * Called when a connection is successfully established with a remote host, providing access to a {@link MessageQueue}.
         *
         * @param queue the {@link MessageQueue} created for the connection.
         */
        void connected(MessageQueue queue);

        /**
         * Called when a connection attempt is refused or fails.
         */
        default void refused() {};
    }
}
