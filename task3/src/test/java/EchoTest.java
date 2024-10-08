import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import task3.EventMessage;
import task3.EventMessageQueue;
import task3.EventQueueBroker;
import task3.impl.LocalEventQueueBroker;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EchoTest {

    static LocalEventQueueBroker server;
    static LocalEventQueueBroker client;

    @BeforeAll
    public static void setup() {
        server = new LocalEventQueueBroker("server");
        client = new LocalEventQueueBroker("client");
    }

    private void populateMessages(List<EventMessage> messages) {
        messages.add(new StringMessage("Hello"));
        messages.add(new StringMessage("World"));
        messages.add(new StringMessage("Goodbye"));
        messages.add(new StringMessage("Life"));
    }

    @RepeatedTest(100)
    public void testEcho() throws InterruptedException {
        server.bind(0, new EchoServer());

        List<EventMessage> sentMessages = new ArrayList<>();
        populateMessages(sentMessages);

        List<EventMessage> receivedMessages = new ArrayList<>();

        client.connect("server", 0, new Connector(sentMessages, receivedMessages));

        Thread.sleep(100);

        assertEquals(sentMessages, receivedMessages);
        server.unbind(0);
    }
}

class EchoServer implements EventQueueBroker.AcceptListener {
    @Override
    public void accepted(EventMessageQueue queue) {
        queue.setListener(new EchoMessageListener(queue));
    }
}

class EchoMessageListener implements EventMessageQueue.Listener {
    private final EventMessageQueue queue;

    public EchoMessageListener(EventMessageQueue queue) {
        this.queue = queue;
    }

    @Override
    public void received(EventMessage msg) {
        queue.send(msg);
    }

    @Override
    public void closed() {
        queue.close();
    }
}

class Connector implements EventQueueBroker.ConnectListener {
    private final List<EventMessage> messagesToSend;
    private final List<EventMessage> receivedMessages;

    public Connector(List<EventMessage> msgToSend, List<EventMessage> receivedMessages) {
        this.messagesToSend = msgToSend;
        this.receivedMessages = receivedMessages;
    }

    @Override
    public void connected(EventMessageQueue queue) {
        var redirector = new TestRedirector(queue, receivedMessages);
        queue.setListener(redirector);
        for (var msg : messagesToSend) {
            queue.send(msg);
        }
    }

    @Override
    public void refused() {
        // Do nothing
    }
}

class TestRedirector implements EventMessageQueue.Listener {
    private final EventMessageQueue queue;
    private final List<EventMessage> receivedMessages;

    public TestRedirector(EventMessageQueue queue, List<EventMessage> receivedMessages) {
        this.queue = queue;
        this.receivedMessages = receivedMessages;
    }

    @Override
    public void received(EventMessage msg) {
        receivedMessages.add(msg);
    }

    @Override
    public void closed() {
        queue.close();
    }
}

class StringMessage extends EventMessage {

    public StringMessage(String str) {
        super(str.getBytes());
    }
}