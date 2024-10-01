package task2;

import org.junit.jupiter.api.Test;
import task1.exceptions.ConnectionFailedException;
import task1.exceptions.DisconnectedException;

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

    @Test
    void sendingBunchOfDataToTheEchoServer() {
        var echoServer = new Task(toto, this::echoServer);
        echoServer.start();
        try {
            var msgQueue = titi.connect("toto", 0);
            for (var testString : testStrings) {
                var message = testString.getBytes();
                msgQueue.send(message, 0, message.length);
                var received = msgQueue.receive();
                assertArrayEquals(message, received);
            }
        } catch (ConnectionFailedException e) {
            fail("Brokers exists, the connection should be successful", e);
        } catch (DisconnectedException e) {
            fail("The echo server should not be disconnected", e);
        }
    }

    public void multiEchoServer() {
        var queueBroker = Task.getQueueBroker();
        while (true) {
            new Task(queueBroker, this::echoServer).start();
        }
    }


    public void echoServer() {
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
