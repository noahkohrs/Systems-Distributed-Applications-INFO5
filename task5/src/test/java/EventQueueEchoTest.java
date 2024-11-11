import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import task4.Broker;
import task4.impl.LocalBroker;
import task5.Message;
import task5.MessageQueue;
import task5.QueueBroker;
import task5.impl.QueueBrokerImpl;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class EventQueueEchoTest {

    public final int NUMBER_OF_CLIENTS = 10;

    @Test
    public void testEcho() throws InterruptedException {

        // Prepare a latch to wait for all clients to receive all messages
        CountDownLatch latch = new CountDownLatch(NUMBER_OF_CLIENTS * getTestSampleMessages().size());
        ArrayList<ArrayList<Message>> messagesCalledBack = new ArrayList<>();

        // Set up the echo server
        Brokers.remoteQueueBroker.bind(1234, queue -> queue.setListener(new EchoListener()));

        // Initialize each client and connect
        for (int i = 0; i < NUMBER_OF_CLIENTS; i++) {
            int clientId = i;
            Brokers.localQueueBroker.connect(
                    "RemoteBroker", 1234,
                    queue -> {
                        var callbackGateway = new ArrayList<Message>();
                        messagesCalledBack.add(callbackGateway);
                        queue.setListener(new ClientListener(clientId, getTestSampleMessages(), callbackGateway, latch));

                        // Send the first message
                        queue.send(getTestSampleMessages().getFirst(), new ClientListener(clientId, getTestSampleMessages(), callbackGateway, latch));
                    }
            );
        }



        // Wait until all messages have been echoed back or timeout
        latch.await(5, TimeUnit.SECONDS);

        Brokers.remoteQueueBroker.unbind(1234);


        // Doing the checks
        for (ArrayList<Message> messages : messagesCalledBack) {
            Assertions.assertArrayEquals(getTestSampleMessages().toArray(), messages.toArray());
        }
    }

    @Test
    public void testBigEcho() throws InterruptedException {

        // Prepare a latch to wait for all clients to receive all messages
        CountDownLatch latch = new CountDownLatch(NUMBER_OF_CLIENTS * getTestSampleBigMessages().size());
        ArrayList<ArrayList<Message>> messagesCalledBack = new ArrayList<>();

        // Set up the echo server
        Brokers.remoteQueueBroker.bind(1234, queue -> queue.setListener(new EchoListener()));

        // Initialize each client and connect
        for (int i = 0; i < NUMBER_OF_CLIENTS; i++) {
            int clientId = i;
            Brokers.localQueueBroker.connect(
                    "RemoteBroker", 1234,
                    queue -> {
                        var callbackGateway = new ArrayList<Message>();
                        messagesCalledBack.add(callbackGateway);
                        queue.setListener(new ClientListener(clientId, getTestSampleBigMessages(), callbackGateway, latch));

                        // Send the first message
                        queue.send(getTestSampleBigMessages().getFirst(), new ClientListener(clientId, getTestSampleBigMessages(), callbackGateway, latch));
                    }
            );
        }



        // Wait until all messages have been echoed back or timeout
        latch.await(5, TimeUnit.SECONDS);

        Brokers.remoteQueueBroker.unbind(1234);


        // Doing the checks
        for (ArrayList<Message> messages : messagesCalledBack) {
            Assertions.assertArrayEquals(getTestSampleBigMessages().toArray(), messages.toArray());
        }
    }

    private ArrayList<Message> getTestSampleMessages() {
        ArrayList<Message> messages = new ArrayList<>();
        messages.add(buildMessageFrom("Hello"));
        messages.add(buildMessageFrom("World"));
        messages.add(buildMessageFrom("This"));
        messages.add(buildMessageFrom("Is"));
        messages.add(buildMessageFrom("A"));
        messages.add(buildMessageFrom("Test"));
        messages.add(buildMessageFrom("Cool, uh?"));
        return messages;
    }

    private Message buildMessageFrom(String content) {
        return new Message(content.getBytes());
    }

    private ArrayList<Message> getTestSampleBigMessages() {
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

    private final int id;
    private final ArrayList<Message> toSend;
    private final ArrayList<Message> received;
    private final CountDownLatch latch;
    // First message is sent from the on connect so we start at 1.
    private int indexToWrite = 1;

    ClientListener(int id, ArrayList<Message> toSend, ArrayList<Message> received, CountDownLatch latch) {
        this.id = id;
        this.toSend = toSend;
        this.received = received;
        this.latch = latch;
    }

    @Override
    public void received(Message message, MessageQueue queue) {
        received.add(message);
        latch.countDown();

        System.out.println("Client " + id + " received message: " + new String(message.getBytes()));

        if (indexToWrite < toSend.size()) {
            queue.send(toSend.get(indexToWrite), this);
            indexToWrite++;
        }
    }

    @Override
    public void closed() {
        System.out.println("Client connection closed.");
    }

    @Override
    public void written(Message message, MessageQueue queue) {
        System.out.println("Message sent: " + new String(message.getBytes()));
    }
}

class EchoListener implements MessageQueue.ReadListener, MessageQueue.WriteListener {

    @Override
    public void received(Message message, MessageQueue queue) {
        System.out.println("Echoing message: " + new String(message.getBytes()));
        queue.send(message, this);  // Echoes back
    }

    @Override
    public void closed() {
        System.out.println("WARNING: The echo server connection was closed.");
    }

    @Override
    public void written(Message message, MessageQueue queue) {
        // Skip
    }
}
