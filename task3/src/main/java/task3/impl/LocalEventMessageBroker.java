package task3.impl;

import task1.exceptions.DisconnectedException;
import task2.MessageQueue;
import task3.EventMessage;
import task3.EventMessageQueue;

public class LocalEventMessageBroker extends EventMessageQueue {

    private final MessageQueue msgsQueue;

    private final Thread receiverThread;

    private Listener listener;

    public LocalEventMessageBroker(MessageQueue msgsQueue) {
        super();
        this.msgsQueue = msgsQueue;
        receiverThread = new Thread(this::receiveMessage);
        receiverThread.start();
    }

    @Override
    public void setListener(Listener l) {
        listener = l;
    }

    @Override
    public boolean send(EventMessage msg) {
        if (msgsQueue.closed()) {
            return false;
        }
        new Thread(() -> sendMessage(msg)).start();
        return true;
    }

    @Override
    public void close() {
        receiverThread.interrupt();
        msgsQueue.close();
    }

    @Override
    public boolean closed() {
        return msgsQueue.closed();
    }

    private void receiveMessage() {
        while (true) {
            try {
                byte[] msg = msgsQueue.receive();
                EventMessage message = new EventMessage(msg);
                EventPump.post(() -> listener.received(message));
            } catch (DisconnectedException e) {
                break;
            }
        }
    }

    private void sendMessage(EventMessage msg) {
        try {
            msgsQueue.send(msg.bytes, 0 , msg.bytes.length);
        } catch (DisconnectedException e) {
            EventPump.post(() -> listener.closed());
        }


    }
}
