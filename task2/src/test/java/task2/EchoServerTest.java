package task2;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task1.exceptions.ConnectionFailedException;
import task1.exceptions.DisconnectedException;
import task1.impl.LocalBroker;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class EchoServerTest extends TitiTotoTesting {

    String[] testStrings = {
        "Hello, World!",
        "This is a test",
        "Teeest",
        "Yet another test",
        "The last test",
        "s"
    };

    @BeforeEach
    void setup() {
        var echoServer = new Task(toto, this::multiEchoServer);
        echoServer.start();
    }

    @AfterEach
    void tearDown() {
        ((LocalBroker) toto.broker).delete();
    }


    @Test
    void sendingBunchOfDataToTheEchoServer() {
        try {
            var msgQueue = titi.connect("toto", 0);
            for (var testString : testStrings) {
                var message = testString.getBytes();
                msgQueue.send(message, 0, message.length);
                var received = msgQueue.receive();
                assertArrayEquals(message, received);
                assertArrayEquals(message, received);
            }
        } catch (ConnectionFailedException e) {
            fail("Brokers exists, the connection should be successful", e);
        } catch (DisconnectedException e) {
            fail("The echo server should not be disconnected", e);
        }
    }

    @Test
    void sendingHugeMessage() {
        byte[] msg = new byte[160000];
        try {
            var msgQueue = titi.connect("toto", 0);
            msgQueue.send(msg, 0, msg.length);
            var received = msgQueue.receive();


        } catch (ConnectionFailedException e) {
        fail("Brokers exists, the connection should be successful", e);
        } catch (DisconnectedException e) {
            fail("The echo server should not be disconnected", e);
        }
    }

    public void multiEchoServer() {
        var queueBroker = Task.getQueueBroker();
        while (true) {
            try {
                var messageQueue = queueBroker.accept(0);
                new Task(queueBroker, () -> echoServer(messageQueue)).start();
            } catch (ConnectionFailedException e) {
                fail();
            }
        }
    }

    public void echoServer(MessageQueue messageQueue) {
        try {
            while (!messageQueue.closed()) {
                var message = messageQueue.receive();
                messageQueue.send(message, 0, message.length);
            }
        } catch (DisconnectedException e) {
            throw new RuntimeException(e);
        }
    }
}
