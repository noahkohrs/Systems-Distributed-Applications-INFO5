package task3.impl;

import task1.exceptions.ConnectionFailedException;
import task1.impl.LocalBroker;
import task2.MessageQueue;
import task2.QueueBroker;
import task2.impl.LocalQueueBroker;
import task3.EventMessageQueue;
import task3.EventQueueBroker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalEventQueueBroker extends EventQueueBroker {
    final Map<Integer, Thread> binders;


    QueueBroker queueBroker;

    /**
     * Constructs a new {@code QueueBroker} instance with the specified name.
     *
     * @param name the name of the queue broker
     */
    public LocalEventQueueBroker(String name) {
        super(name);
        binders = new ConcurrentHashMap<>();
        queueBroker = new LocalQueueBroker(new LocalBroker(name));

    }

    @Override
    public synchronized boolean bind(int port, AcceptListener listener) {
        if (binders.containsKey(port)) {
            return false;
        }

        Thread t = new Thread(() -> {
            while (true) {
                try {
                    MessageQueue msgsQueue = queueBroker.accept(port);
                    EventMessageQueue queue = new LocalEventMessageQueue(msgsQueue);
                    EventPump.post(new EventTask("Accepting", () -> listener.accepted(queue)));
                } catch (Exception e) {
                    break;
                }
            }
        });
        binders.put(port, t);
        t.start();
        return true;
    }

    @Override
    public synchronized boolean unbind(int port) {
        Thread t = binders.remove(port);
        if (t == null) {
            return false;
        }
        t.interrupt();
        return true;
    }

    @Override
    public boolean connect(String name, int port, ConnectListener listener) {
        new Thread(() -> {
            try {
                MessageQueue msgsQueue = queueBroker.connect(name, port);
                EventMessageQueue queue = new LocalEventMessageQueue(msgsQueue);
                EventPump.post(new EventTask("Connecting", () -> listener.connected(queue)));
            } catch (ConnectionFailedException e) {
                EventPump.post(new EventTask("Refusing", listener::refused));
            }
        }).start();
        return true;
    }
}
