package task3;

/**
 * The {@link EventQueueBroker} is a queue-based messaging broker system.
 * <br>
 * This broker manages connections to message queues via ports, providing mechanisms for binding, unbinding, and connecting
 * to named queues over specific ports.
 * <br>
 * It acts as an intermediary between message producers and consumers, allowing for
 * asynchronous message passing.
 * <br>
 * A {@link EventQueueBroker} is thread-safe and can be used to manage multiple message queues concurrently.
 *
 * @see EventMessageQueue
 */
public abstract class EventQueueBroker {
    /** The name of this QueueBroker instance, used for identification purposes. */
    public final String name;

    /**
     * Constructs a new {@code QueueBroker} instance with the specified name.
     *
     * @param name the name of the queue broker
     */
    public EventQueueBroker(String name) {
        this.name = name;
    }

    /**
     * Listener interface to handle acceptance of incoming connections to a bound queue.
     */
    public interface AcceptListener {
        /**
         * Called when a new connection has been accepted to a bound {@link EventMessageQueue}.
         * <br>
         * The function will never be executed concurrently with other events.
         *
         * @param queue the accepted {@link EventMessageQueue}
         */
        void accepted(EventMessageQueue queue);
    }

    /**
     * Binds the queue broker to the specified port and listens for incoming connections. When a connection
     * is accepted, the provided {@code AcceptListener} will be invoked.
     * <br>
     * Multiple connections can result from a single bind operation.
     * Please see {@link EventQueueBroker#unbind(int)} to stop accepting connections on a specific port.
     *
     * @param port     the port number to bind to
     * @param listener the listener to handle accepted connections
     * @return {@code true} if the bind operation was successful, {@code false} otherwise
     */
    public abstract boolean bind(int port, AcceptListener listener);

    /**
     * Unbinds the queue broker from the specified port, stopping any further acceptance of connections on that port.
     *
     * @param port the port number to unbind
     * @return {@code true} if the unbind operation was successful, {@code false} otherwise
     */
    public abstract boolean unbind(int port);

    /**
     * Listener interface to handle connection events when connecting to a remote or local queue.
     */
    public interface ConnectListener {
        /**
         * Called when a connection to the {@link EventMessageQueue} is successfully established.
         * <br>
         * The function will never be executed concurrently with other events.
         *
         * @param queue the connected {@link EventMessageQueue}
         */
        void connected(EventMessageQueue queue);

        /**
         * Called when the connection attempt has been refused.
         * <br>
         * The function will never be executed concurrently with other events.
         *
         */
        void refused();
    }

    /**
     * Connects the queue broker to a remote or local queue identified by the given name and port. When a connection
     * is established, the {@code ConnectListener} will be invoked.
     *
     * @param name     the name of the queue to connect to
     * @param port     the port number to connect through
     * @param listener the listener to handle connection events
     * @return {@code true} if the connection was initiated successfully, {@code false} otherwise
     */
    public abstract boolean connect(String name, int port, ConnectListener listener);
}

