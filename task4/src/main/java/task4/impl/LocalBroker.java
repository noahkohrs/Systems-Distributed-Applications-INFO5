package task4.impl;

import task4.Broker;
import task4.EventPump;
import task4.EventTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalBroker extends Broker {

    Map<Integer, AcceptListener> currentBindings = new HashMap<>();

    /**
     * Create a new broker with the given name.
     * <br>
     * A broker is a network abstraction that can accept incoming connections on a given port and connect to hosts.
     *
     * @param name the name of the broker.
     */
    public LocalBroker(String name) {
        super(name);
        BrokerManager.addBroker(this);

    }

    /**
     * @param port     the port to listen on.
     * @param listener the listener to handle accepted connections
     * @throws IllegalStateException if the connection could not be established (check if the port is already in use).
     */
    @Override
    public synchronized void bind(int port, AcceptListener listener) {
        if (currentBindings.containsKey(port)) {
            throw new IllegalStateException("Port already in use.");
        }
        currentBindings.put(port, listener);
    }

    @Override
    public synchronized void unbind(int port) {
        currentBindings.remove(port);
    }


    @Override
    public synchronized void connect(String host, int port, ConnectListener listener) {
        var oppositeBroker = BrokerManager.getBroker(host);
        if (oppositeBroker != null) {
            oppositeBroker._accept(this, port, listener);
        } else {
            EventPump.post("Connection to" + host + "failed.", listener::refused);
        }

    }

    private synchronized void _accept(LocalBroker otherBroker, int port, ConnectListener listener) {

        if (currentBindings.containsKey(port)) {
            LocalChannel channelConnect = new LocalChannel(otherBroker, 1024);
            LocalChannel channelAccept = new LocalChannel(this, 1024);
            LocalChannel.linkChannels(channelConnect, channelAccept);
            var taskAccept = new EventTask("Accepted", () -> listener.connected(channelAccept));
            var taskConnect = new EventTask("Connected", () -> currentBindings.get(port).accepted(channelConnect));
            EventPump.post(List.of(taskAccept, taskConnect));
        } else {
            EventPump.post("Connection refused", listener::refused);
        }
    }

    public synchronized void delete() {
        BrokerManager.removeBroker(this.name);
    }
}
