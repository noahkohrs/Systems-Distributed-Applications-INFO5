import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.Random;
import task4.Broker;
import task4.impl.LocalBroker;
import task5.Message;
import task5.MessageQueue;
import task5.QueueBroker;
import task5.impl.QueueBrokerImpl;

import java.util.ArrayList;

public class EventQueueEchoTestBigMessages {

    public final int NUMBER_OF_CLIENT = 10;
    @Test
    public void testEcho() {
        Broker localBroker = new LocalBroker("LocalBroker");
        QueueBroker localQueueBroker = new QueueBrokerImpl(localBroker);
        localQueueBroker.bind(
                1234,
                queue -> queue.setListener(new EchoListener(queue))
        );

        ArrayList<ArrayList<Message>> messagesCalledBack = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_CLIENT; i++) {
            int finalI = i;
            localQueueBroker.connect(
                "LocalBroker",
                1234,
                    queue -> {
                        var callbackGateway = new ArrayList<Message>();
                        messagesCalledBack.add(callbackGateway);
                        queue.setListener(new ClientListener(finalI, getTestSampleMessages(), callbackGateway));
                    }
            );
        }

        // Wait until each connection happened
        for (int i = 0; i < NUMBER_OF_CLIENT; i++) {
            while (messagesCalledBack.size() < NUMBER_OF_CLIENT) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // Wait until messaging is done
        for (int i = 0; i < NUMBER_OF_CLIENT; i++) {
            while (messagesCalledBack.get(i).size() < getTestSampleMessages().size()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // Assert
        for (int i = 0; i < NUMBER_OF_CLIENT; i++) {
            ArrayList<Message> messages = messagesCalledBack.get(i);
            for (int j = 0; j < getTestSampleMessages().size(); j++) {
                Assertions.assertArrayEquals(getTestSampleMessages().toArray(), messages.toArray());
            }
        }
    }


    private ArrayList<Message> getTestSampleMessages() {
        ArrayList<Message> messages = new ArrayList<>();
        
        // Creating large messages with random data.
        messages.add(buildLargeMessageFrom(1024 * 10));  // 10 KB message
        messages.add(buildLargeMessageFrom(1024 * 50));  // 50 KB message
        messages.add(buildLargeMessageFrom(1024 * 100)); // 100 KB message
        messages.add(buildLargeMessageFrom(1024 * 200)); // 200 KB message
        messages.add(buildLargeMessageFrom(1024 * 500)); // 500 KB message
        messages.add(buildLargeMessageFrom(1024 * 1024)); // 1 MB message
        messages.add(buildLargeMessageFrom(1024 * 1024 * 2)); // 2 MB message
        
        return messages;
    }
    
    private Message buildLargeMessageFrom(int sizeInBytes) {
        byte[] largeMessage = new byte[sizeInBytes];
        
        Random random = new Random();
        
        // Fill the byte array with random data
        random.nextBytes(largeMessage);
        
        return new Message(largeMessage);
    }
}


class ClientListener implements MessageQueue.ReadListener, MessageQueue.WriteListener {

    // used for debug
    private final int N;
    private final ArrayList<Message> toSend;
    private final ArrayList<Message> received;
    // First message is sent from the main program so we start at 1.
    int indexToWrite = 1;

    ClientListener(int N, ArrayList<Message> toSend, ArrayList<Message> received) {
        this.N = N;
        this.toSend = toSend;
        this.received = received;
    }

    @Override
    public void received(Message message, MessageQueue queue) {
        received.add(message);
    }

    @Override
    public void closed() {
        System.out.println("WARNING: The connection was closed.");
    }

    @Override
    public void written(Message message, MessageQueue queue) {
        if (indexToWrite < toSend.size()) {
            queue.send(toSend.get(indexToWrite), this);
            indexToWrite++;
        }
    }
}

class EchoListener implements MessageQueue.ReadListener, MessageQueue.WriteListener {

    private final MessageQueue queue;

    EchoListener(MessageQueue queue) {
        this.queue = queue;
    }

    @Override
    public void received(Message message, MessageQueue queue) {
        // Echoes the received msg
        queue.send(message, this);
    }

    @Override
    public void closed() {
        System.out.println("WARNING: The echo server connection was closed.");
    }

    @Override
    public void written(Message message, MessageQueue queue) {
    }
}