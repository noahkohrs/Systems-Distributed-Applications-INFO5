import org.junit.jupiter.api.Test;
import task3.EventMessage;
import task3.EventMessageQueue;
import task3.EventQueueBroker;
import task3.impl.LocalEventQueueBroker;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EchoTest {

    @Test
    public void testEcho() throws InterruptedException {
        var server = new LocalEventQueueBroker("server");
        var client = new LocalEventQueueBroker("client");

        server.bind(0, new EchoServer());

        List<EventMessage> sentMessages = List.of(
                new EventMessage("Hello".getBytes()),
                new EventMessage("World".getBytes())
        );
        List<EventMessage> receivedMessages = new ArrayList<>();

        client.connect("server", 0, new Connector(sentMessages, receivedMessages));

        Thread.sleep(1000);

        assertEquals(sentMessages, receivedMessages);
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
        messagesToSend.forEach(queue::send);
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