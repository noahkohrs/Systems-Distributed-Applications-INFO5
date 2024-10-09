package task2.impl;

import task1.Broker;
import task1.exceptions.ConnectionFailedException;
import task2.MessageQueue;
import task2.QueueBroker;

public class LocalQueueBroker extends QueueBroker {
    public LocalQueueBroker(Broker broker) {
        super(broker);
    }

    @Override
    public String name() {
        return broker.name;
    }

    @Override
    public MessageQueue accept(int port) throws ConnectionFailedException {
        return new LocalMessageQueue(broker.accept(port), this);
    }

    @Override
    public MessageQueue connect(String name, int port) throws ConnectionFailedException {
        return new LocalMessageQueue(broker.connect(name, port), this);
    }
}
