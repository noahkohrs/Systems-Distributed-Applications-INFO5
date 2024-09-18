package task1;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class EchoServerTests {

    public static String HOST_NAME = "localhost";
    public static int HOST_PORT = 1711;

    @BeforeAll
    static void tearsUp() {
        var broker = new FillerBroker(HOST_NAME);
        new Task(broker, () -> {
            while (true) {
                // Accept incoming connections.
                Channel channel = broker.accept(HOST_PORT);
                if (channel != null) {
                    // Echo the received data until it is disconnected.
                    new Task(broker, () -> {
                        byte[] buffer = new byte[1024];
                        while (true) {
                            int read = channel.read(buffer, 0, buffer.length);
                            if (read == -1) {
                                channel.disconnect();
                                return;
                            }
                            int write = channel.write(buffer, 0, read);
                            if (write == -1) {
                                channel.disconnect();
                                return;
                            }
                            System.out.print(new String(buffer, 0, read));
                        }
                    }).start();
                }
            }
        }).start();
    }

    @Test
    void simpleTestClient() {
        var broker = new FillerBroker("client");
        var channel = broker.connect(HOST_NAME, HOST_PORT);
        for (int i = 1; i <= 255; i++) {
            byte[] buffer = new byte[]{(byte) i};
            channel.write(buffer, 0, buffer.length);
            byte[] readBuffer = new byte[1];
            int read = channel.read(readBuffer, 0, 1);
            assertEquals(1, read);
            assertEquals(i, readBuffer[0]);
        }
        channel.disconnect();
    }
}

/**
 * Should be replaced by the real implementation of the broker once it's ready.
 */
class FillerBroker extends Broker {
    public FillerBroker(String name) {
        super(name);
    }

    @Override
    public Channel accept(int port) {
        return null;
    }

    @Override
    public Channel connect(String host, int port) {
        return null;
    }
}