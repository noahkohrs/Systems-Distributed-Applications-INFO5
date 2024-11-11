package task5.impl;

import task4.Broker;
import task4.Channel;
import task5.QueueBroker;

public class QueueBrokerImpl extends QueueBroker {

    /**
     * Create a new QueueBroker with the given name.
     *
     * @param broker the Broker it derives from.
     */
    public QueueBrokerImpl(Broker broker) {
        super(broker);
    }

    @Override
    public void bind(int port, AcceptListener listener) {
        parentBroker.bind(port, channel -> listener.accepted(new MessageQueueImpl(channel)));
    }

    @Override
    public void unbind(int port) {
        parentBroker.unbind(port);
    }

    @Override
    public void connect(String host, int port, ConnectListener listener) {
        parentBroker.connect(host, port, new Broker.ConnectListener() {
            @Override
            public void connected(Channel channel) {
                listener.connected(new MessageQueueImpl(channel));
            }

            @Override
            public void refused() {
                listener.refused();
            }
        });
    }
}
