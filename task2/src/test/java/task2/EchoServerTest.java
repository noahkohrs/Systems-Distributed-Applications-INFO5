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
        var echoServer = new Task(toto, EchoServerTask::new);
        echoServer.start();
        for (var testString : testStrings) {
            try {
                var msgQueue = titi.connect("toto", 0);
                var message = testString.getBytes();
                msgQueue.send(message, 0, message.length);
                var received = msgQueue.receive();
                assertArrayEquals(message, received);
            } catch (ConnectionFailedException e) {
                fail("Brokers exists, the connection should be successful", e);
            } catch (DisconnectedException e) {
                fail("The echo server should not be disconnected", e);
            }
        }
    }
}
