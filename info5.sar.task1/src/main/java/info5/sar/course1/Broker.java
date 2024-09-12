package info5.sar.course1;

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
        this.name = name;
    }

    /**
     * Accept an incoming connection on the given port.
     * <br>
     * This is a blocking operation, it will return only once a connection has been accepted.
     *
     * @param port the port to listen on.
     * @return a channel representing the connection and null if the connection failed.
     */
    public abstract Channel accept(int port);

    /**
     * Connect to a remote host on the given port.
     * <br>
     * This is a blocking operation, it will return only once a connection has been established.
     *
     * @param host the host to connect to.
     * @param port the port to connect to.
     * @return a channel representing the connection to the remote host and null if the connection failed.
     */
    public abstract Channel connect(String host, int port);
}

