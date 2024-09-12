package info5.sar.course1;

public abstract class Broker {
    /**
     * The name of the broker.
     */
    public final String name;

    /**
     * Create a new broker with the given name.
     * A broker is a network abstraction that can accept incoming connections on a given port and connect to hosts.
     *
     * @param name the name of the broker.
     */
    Broker(String name) {
        this.name = name;
    }

    /**
     * Accept an incoming connection on the given port.
     *
     * @param port the port to listen on.
     * @return a channel representing the connection.
     */
    abstract Channel accept(int port);

    /**
     * Connect to a remote host on the given port.
     *
     * @param host the host to connect to.
     * @param port the port to connect to.
     * @return a channel representing the connection.
     */
    abstract Channel connect(String host, int port);
}

