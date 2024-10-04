package task3.impl;

import task3.EventMessage;
import task3.EventMessageQueue;
import task3.EventQueueBroker;

public class LocalEventMessageBroker extends EventMessageQueue {
    @Override
    public void setListener(Listener l) {

    }

    @Override
    public boolean send(EventMessage msg) {
        return false;
    }

    @Override
    public void close() {

    }

    @Override
    public boolean closed() {
        return false;
    }
}
