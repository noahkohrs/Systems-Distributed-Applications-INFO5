import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import task5.Message;
import task5.MessageQueue;
import task5.QueueBroker;

import java.util.ArrayList;

public class EventQueueCloseConnectionTest {

    @Test
    public void testCloseConnection() {
        Brokers.localQueueBroker.bind(1234, queue -> queue.setListener(new EchoListener(queue)));

        ArrayList<Message> receivedMessages = new ArrayList<>();
        Brokers.localQueueBroker.connect("LocalBroker", 1234,
            new QueueBroker.ConnectListener() {
                @Override
                public void connected(MessageQueue queue) {
                    queue.setListener(new MessageQueue.ReadListener() {
                        @Override
                        public void received(Message message, MessageQueue queue) {
                            receivedMessages.add(message);
                            // Close the connection after receiving the first message
                            queue.close();
                        }

                        @Override
                        public void closed() {
                            System.out.println("Connection closed.");
                        }
                    });

                    // Send a message
                    queue.send(new Message("Test message".getBytes()), new MessageQueue.WriteListener() {
                        @Override
                        public void written(Message message, MessageQueue queue) {
                            // No more writes needed as we're only sending one message
                        }
                    });
                }

                @Override
                public void refused() {
                    Assertions.fail("Connection should not fail.");
                }
            }
        );

        // Wait until the message is received and the connection is closed
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Assert that the message was received
        Assertions.assertEquals(1, receivedMessages.size());
        Assertions.assertEquals("Test message", new String(receivedMessages.get(0).getBytes()));

        // Now check that no new messages are received after closing the connection
        int initialSize = receivedMessages.size();
        try {
            Thread.sleep(100); // Wait a bit to check if any new messages are received
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals(initialSize, receivedMessages.size(), "No new messages should be received after closing.");
    }

    // Define EchoListener here
    private static class EchoListener implements MessageQueue.ReadListener, MessageQueue.WriteListener {

        private final MessageQueue queue;

        EchoListener(MessageQueue queue) {
            this.queue = queue;
        }

        @Override
        public void received(Message message, MessageQueue queue) {
            // Echoes the received message
            queue.send(message, this);
        }

        @Override
        public void closed() {
            System.out.println("WARNING: The echo server connection was closed.");
        }

        @Override
        public void written(Message message, MessageQueue queue) {
            // No actions needed for echoing the message
        }
    }
}
