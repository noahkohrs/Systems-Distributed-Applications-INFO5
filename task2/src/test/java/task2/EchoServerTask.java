package task2;

import task1.exceptions.ConnectionFailedException;
import task1.exceptions.DisconnectedException;

public class EchoServerTask implements Runnable {
    @Override
    public void run() {
        var queueBroker = Task.getQueueBroker();
        while (true) {
            new Task(queueBroker, new EchoTask()).start();
        }
    }
}

class EchoTask implements Runnable {

    @Override
    public void run() {
        var queueBroker = Task.getQueueBroker();
        try {
            var messageQueue = queueBroker.accept(0);
            while (!messageQueue.closed()) {
                var message = messageQueue.receive();
                messageQueue.send(message, 0, message.length);
            }
        } catch (ConnectionFailedException | DisconnectedException e) {
            throw new RuntimeException(e);
        }

    }
}
