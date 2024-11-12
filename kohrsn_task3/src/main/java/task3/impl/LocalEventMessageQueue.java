package task3.impl;

import task2.MessageQueue;
import task2.Task;
import task3.EventMessage;
import task3.EventMessageQueue;

import java.util.LinkedList;
import java.util.Queue;

public class LocalEventMessageQueue extends EventMessageQueue {

    private final MessageQueue msgsQueue;

    private final Task receiverThread;
    private final Task senderThread;

    Queue<EventMessage> messageSending;


    private Listener listener;

    public LocalEventMessageQueue(MessageQueue msgsQueue) {
        super();
        this.msgsQueue = msgsQueue;
        receiverThread = new Task(msgsQueue.parentQueueBroker, this::receiveMessages);
        senderThread = new Task(msgsQueue.parentQueueBroker, this::sendMessages);
        messageSending = new LinkedList<>();
        senderThread.start();
    }

    @Override
    public void setListener(Listener l) {
        if (!receiverThread.isAlive() && !receiverThread.isInterrupted())
            receiverThread.start();
        listener = l;
    }

    @Override
    public boolean send(EventMessage msg) {
        if (msgsQueue.closed()) {
            return false;
        }
        synchronized (messageSending) {
            messageSending.add(msg);
            messageSending.notify();
        }
        return true;
    }

    @Override
    public void close() {
        receiverThread.interrupt();
        senderThread.interrupt();
        // Non-blocking operation
        msgsQueue.close();
        if (listener != null) EventPump.post("Closed", listener::closed);
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
                if (listener != null) EventPump.post("Receiving " + message, () -> listener.received(message));
            } catch (Exception e) {
                if (listener != null) EventPump.post("Closed", listener::closed);
                break;
            }
        }
    }

    // Inorder to have a FIFO ordering
    private void sendMessages() {
        while (true) {
            try {
                EventMessage msg;
                synchronized (messageSending) {
                    if (!messageSending.isEmpty()) {
                        msg = messageSending.poll();
                    } else {
                        messageSending.wait();
                        continue;
                    }
                }
                msgsQueue.send(msg.getBytes(), 0, msg.getLength());
            } catch (Exception e) {
                break;
            }
        }

    }
}
