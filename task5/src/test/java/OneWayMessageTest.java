import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import task4.Broker;
import task4.impl.LocalBroker;
import task5.Message;
import task5.MessageQueue;
import task5.QueueBroker;
import task5.impl.QueueBrokerImpl;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Simpler test than the Echo server since it's
 */
public class OneWayMessageTest {

    @Test
    public void testOneWayMessage() throws InterruptedException {
        Broker localBroker = new LocalBroker("LocalBroker");
        Broker remoteBroker = new LocalBroker("RemoteBroker");
        QueueBroker localQueueBroker = new QueueBrokerImpl(localBroker);
        QueueBroker remoteQueueBroker = new QueueBrokerImpl(remoteBroker);

        // Prepare a latch to wait until all messages are received
        ArrayList<Message> receivedMessages = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(getTestSampleMessages().size());

        // Set up a one-way server that only receives messages
        remoteQueueBroker.bind(1234, queue -> {
            queue.setListener(new ServerListener(receivedMessages, latch));
        });

        // Connect the client and start sending messages
        localQueueBroker.connect("RemoteBroker", 1234, queue -> {
            ArrayList<Message> messagesToSend = getTestSampleMessages();
            for (Message message : messagesToSend) {
                queue.send(message, new ClientWriteListener());
            }
        });

        // Wait until all messages have been received or timeout
        latch.await(5, TimeUnit.SECONDS);

        // Verify the received messages
        Assertions.assertArrayEquals(getTestSampleMessages().toArray(), receivedMessages.toArray());
    }

    private ArrayList<Message> getTestSampleMessages() {
        ArrayList<Message> messages = new ArrayList<>();
        messages.add(buildMessageFrom("Hello"));
        messages.add(buildMessageFrom("World"));
        messages.add(buildMessageFrom("One-Way"));
        messages.add(buildMessageFrom("Message"));
        messages.add(buildMessageFrom("Test"));
        return messages;
    }

    private Message buildMessageFrom(String content) {
        return new Message(content.getBytes());
    }
}

// Server listener that only receives messages
class ServerListener implements MessageQueue.ReadListener {

    private final ArrayList<Message> receivedMessages;
    private final CountDownLatch latch;

    ServerListener(ArrayList<Message> receivedMessages, CountDownLatch latch) {
        this.receivedMessages = receivedMessages;
        this.latch = latch;
    }

    @Override
    public void received(Message message, MessageQueue queue) {
        System.out.println("Server received message: " + new String(message.getBytes()));
        receivedMessages.add(message);
        latch.countDown();
    }

    @Override
    public void closed() {
        System.out.println("Server connection closed.");
    }
}

// Client write listener for sending messages
class ClientWriteListener implements MessageQueue.WriteListener {

    @Override
    public void written(Message message, MessageQueue queue) {
        System.out.println("Client sent message: " + new String(message.getBytes()));
    }
}
