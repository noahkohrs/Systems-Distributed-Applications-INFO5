package task3.impl;

import task1.exceptions.DisconnectedException;
import task2.MessageQueue;
import task3.EventMessage;
import task3.EventMessageQueue;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LocalEventMessageQueue extends EventMessageQueue {

    private final MessageQueue msgsQueue;

    private final Thread receiverThread;
    private final Thread senderThread;

    Queue<EventMessage> messageSending;


    private Listener listener;

    public LocalEventMessageQueue(MessageQueue msgsQueue) {
        super();
        this.msgsQueue = msgsQueue;
        receiverThread = new Thread(this::receiveMessages);
        senderThread = new Thread(this::sendMessages);
        messageSending = new ConcurrentLinkedQueue<>();
        receiverThread.start();
        senderThread.start();
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
        messageSending.add(msg);
        return true;
    }

    @Override
    public void close() {
        receiverThread.interrupt();
        senderThread.interrupt();
        msgsQueue.close();
    }

    @Override
    public boolean closed() {
        return msgsQueue.closed();
    }

    private void receiveMessages() {
        while (true) {
            try {
                byte[] msg = msgsQueue.receive();
                EventMessage message = new EventMessage(msg);
                EventPump.post(new EventTask("Receiving " + message, () -> listener.received(message)));
            } catch (Exception e) {
                break;
            }
        }
    }

    // Inorder to have a FIFO ordering
    private void sendMessages() {
        while (true) {
            try {
                if (messageSending.isEmpty()) {
                    Thread.sleep(10);
                    continue;
                }
                EventMessage msg = messageSending.poll();
                msgsQueue.send(msg.getBytes(), 0, msg.getLength());
                EventPump.post(new EventTask("Sending " + msg, () -> {
                }));
            } catch (Exception e) {
                break;
            }
        }

    }
}
