package task4;

/**
 * A broker is a network abstraction that can accept incoming connections on a given port and connect to hosts.
 * This is an abstract class that represents a broker.
 *
 * @see Channel
 */
public abstract class Broker {
    /**
     * The name of the broker.
     * <br>
     * Should be unique and to identify the broker.
     */
    public final String name;

    /**
     * Create a new broker with the given name.
     * <br>
     * A broker is a network abstraction that can accept incoming connections on a given port and connect to hosts.
     *
     * @param name the name of the broker.
     */
    public Broker(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        this.name = name;
    }

    /**
     * Accept an incoming connection on the given port.
     * <br>
     * This is a non-blocking operation, it will return immediately.
     *
     * @param port the port to listen on.
     * @throws IllegalStateException if the connection could not be established (check if the port is already in use).
     */
    public abstract void bind(int port, AcceptListener listener);

    public abstract void unbind(int port);

    public interface AcceptListener {
        /**
         * Called when a new connection has been accepted to a bound {@link Channel}.
         * <br>
         * The function will never be executed concurrently with other events.
         *
         * @param channel the resulting {@link Channel}
         */
        void accepted(Channel channel);
    }
    /**
     * Connect to a remote host on the given port.
     * <br>
     * This is a non-blocking operation, it will return immediately.
     *
     * @param host the host to connect to.
     * @param port the port to connect to.
     */
    public abstract void connect(String host, int port, ConnectListener listener);

    public interface ConnectListener {
        /**
         * Called when a connection by the {@link Channel} is successfully established.
         * <br>
         * The function will never be executed concurrently with other events.
         *
         * @param channel the connected
         */
        void connected(Channel channel);

        void refused();
    }
}