import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import task4.Broker;
import task4.impl.LocalBroker;
import task5.Message;
import task5.MessageQueue;
import task5.QueueBroker;
import task5.impl.QueueBrokerImpl;

import java.util.ArrayList;

public class EventQueueEchoTest {

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


    // might need to be changed to random message generation later on
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

    private Message buildMessageFrom(String message) {
        return new Message(message.getBytes());
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