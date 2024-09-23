package task1;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import task1.exceptions.ConnectionFailedException;
import task1.exceptions.DisconnectedException;
import task1.impl.LocalBroker;

import static org.junit.jupiter.api.Assertions.*;
class EchoServerTests {

    public static String HOST_NAME = "localhost";
    public static int HOST_PORT = 1711;

    @BeforeAll
    static void tearsUp() {
        var broker = new LocalBroker(HOST_NAME);
        new Task(broker, () -> {
            while (true) {
                // Accept incoming connections.
                Channel channel = null;
                try {
                    channel = broker.accept(HOST_PORT);
                } catch (ConnectionFailedException e) {
                    fail("Connection failed");
                }
                if (channel != null) {
                    // Echo the received data until it is disconnected.
                    Channel finalChannel = channel;
                    new Task(broker, () -> {
                        byte[] buffer = new byte[1];
                        while (true) {
                            try {
                                int read = finalChannel.read(buffer, 0, buffer.length);
                                int write = finalChannel.write(buffer, 0, read);
                                assertEquals(read, write);
                            } catch (DisconnectedException e) {
                                finalChannel.disconnect();
                                return;
                            }
                        }
                    }).start();
                }
            }
        }).start();
    }

    @Test
    void simpleTestClient() {
        var broker = new LocalBroker("client");
        Channel channel = null;
        try {
            channel = broker.connect(HOST_NAME, HOST_PORT);
        } catch (ConnectionFailedException e) {
            fail("Connection failed");
        }
        byte[] readBuffer = new byte[1];
        for (int i = 0; i <= 255; i++) {
            byte[] buffer = new byte[]{(byte) i};
            try {
                int write = channel.write(buffer, 0, buffer.length);
                assertEquals(write, 1);
                int read = channel.read(readBuffer, 0, 1);
                assertEquals(1, read);
                assertEquals(buffer[0], readBuffer[0]);
            } catch (DisconnectedException e) {
                fail("Channel disconnected");
            }
        }
        channel.disconnect();
    }
}