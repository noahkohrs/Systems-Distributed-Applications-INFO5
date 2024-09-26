package task2;

import task1.Broker;
import task1.exceptions.ConnectionFailedException;

/**
 * A {@link QueueBroker} is a network abstraction that can accept incoming connections on a given port and connect to hosts in order.
 * <br>
 * The connections resulting for a connection are represented by {@link MessageQueue}.
 */
public abstract class QueueBroker {

    final Broker broker;

    QueueBroker(Broker broker) {
        this.broker = broker;
    }

    /**
     * Get the name of the message queue broker.
     *
     * @return the name of the message queue broker.
     */
    abstract String name();

    /**
     * Accept a connection on the given port.
     *
     * @param port the port to accept the connection on.
     * @return the message queue associated with the connection.
     */
    abstract MessageQueue accept(int port) throws ConnectionFailedException;

    /**
     * Connect to the message queue broker with the given name.
     *
     * @param name the name of the message queue broker.
     * @param port the port to connect to.
     * @return the message queue associated with the connection.
     */
    abstract MessageQueue connect(String name, int port) throws ConnectionFailedException;
}
