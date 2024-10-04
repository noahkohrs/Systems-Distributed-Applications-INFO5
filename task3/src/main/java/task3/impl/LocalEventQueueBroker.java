package task3.impl;

import task3.EventQueueBroker;

public class LocalEventQueueBroker extends EventQueueBroker {
    /**
     * Constructs a new {@code QueueBroker} instance with the specified name.
     *
     * @param name the name of the queue broker
     */
    public LocalEventQueueBroker(String name) {
        super(name);
    }

    @Override
    public boolean bind(int port, AcceptListener listener) {
        return false;
    }

    @Override
    public boolean unbind(int port) {
        return false;
    }

    @Override
    public boolean connect(String name, int port, ConnectListener listener) {
        return false;
    }
}
