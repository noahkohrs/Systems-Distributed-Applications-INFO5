package task1;

import org.junit.jupiter.api.*;
import task1.exceptions.ConnectionFailedException;
import task1.exceptions.DisconnectedException;
import task1.impl.LocalBroker;

import static org.junit.jupiter.api.Assertions.*;

class EchoServerTest {

    static final String HOST_NAME = "server";
    static final String CLIENT_NAME = "client";
    static final int HOST_PORT = 1711;
    static final Broker server = new LocalBroker(HOST_NAME);
    static final Broker client = new LocalBroker(CLIENT_NAME);

    private static void echo() {
        while (true) {
            Channel channel = null;
            try {
                channel = server.accept(HOST_PORT);
            } catch (ConnectionFailedException e) {
                fail("Connection failed due to unexpected exception");
            }
            if (channel != null) {
                Channel finalChannel = channel;
                new Task(server, () -> {
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
    };

    @AfterAll
    static void tearDown() {
        ((LocalBroker)server).delete();
        ((LocalBroker)client).delete();
    }

    @BeforeAll
    static void tearsUp() {
        new Task(server, EchoServerTest::echo).start();
    }

    @Nested
    class LimitTests {

        static final int NUMBER_OF_SIMULTANEOUS_CONNECTIONS = 120;

        /**
         * Test that a broker can handle multiple clients connections (10)
         */
        @Test
        void multipleQueuingClientConnexion() {
            var bytesRead = new byte[NUMBER_OF_SIMULTANEOUS_CONNECTIONS];
            var tasks = new Task[NUMBER_OF_SIMULTANEOUS_CONNECTIONS];
            // Creating a bunch of task that will connect and make a simple task to the server.
            for (int i = 0; i < NUMBER_OF_SIMULTANEOUS_CONNECTIONS; i++) {
                final byte finalI = (byte) i;
                tasks[i] = new Task(client, () -> {
                    try {
                        Channel channel = Task.getBroker().connect(HOST_NAME, HOST_PORT);
                        byte[] buffer = new byte[]{finalI};
                        int write = channel.write(buffer, 0, buffer.length);
                        int read = channel.read(bytesRead, finalI, 1);
                    } catch (ConnectionFailedException e) {
                        fail("Connection failed");
                    } catch (DisconnectedException e) {
                        fail("Channel disconnected for no reason");
                    }
                });
            }
            for (Task task : tasks) {
                task.start();
            }
            // Wait for all the tasks to finish.
            for (Task task : tasks) {
                try {
                    task.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            // Check that all the bytes have been read.
            for (int i = 0; i < NUMBER_OF_SIMULTANEOUS_CONNECTIONS; i++) {
                assertEquals(i, bytesRead[i]);
            }
        }
    }

    @RepeatedTest(100)
    void defaultTest() {
        Channel channel = null;
        try {
            channel = client.connect(HOST_NAME, HOST_PORT);
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