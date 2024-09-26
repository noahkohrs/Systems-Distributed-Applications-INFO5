package task1.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import task1.Channel;
import task1.Task;
import task1.exceptions.ConnectionFailedException;
import task1.exceptions.DisconnectedException;

import static org.junit.jupiter.api.Assertions.*;

public class LocalChannelTest {

    @Nested
    class BasicTests {

    }

    @Nested
    class AdvancedTests {

        LocalBroker server;
        LocalBroker client;

        @BeforeEach
        void setUp() {
            server = new LocalBroker("server");
            client = new LocalBroker("client");
        }

        @AfterEach
        void tearDown() {
            server.delete();
            client.delete();
        }

        @Test
        void disconnectWhileBufferStillHasToRead() {
            var sender = new Task(server, () -> {
                try {
                    var channel = client.accept(666);
                    channel.write(new byte[] {1}, 0, 1);
                    channel.disconnect();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            sender.start();

            Channel channel;
            try {
                channel = server.connect("client", 666);
            } catch (ConnectionFailedException e) {
                throw new RuntimeException(e);
            }
            try {
                assertEquals(1, channel.read(new byte[12], 0, 12));
            } catch (DisconnectedException e) {
                throw new RuntimeException(e);
            }

            assertThrows(DisconnectedException.class, () -> channel.read(new byte[1], 0, 1));
        }

        @Test
        void readingBeforeOppositeDisconnects() {
            var sender = new Task(server, () -> {
                try {
                    var channel = client.accept(666);
                    Thread.sleep(150);
                    channel.disconnect();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            sender.start();

            Channel channel;
            try {
                channel = server.connect("client", 666);
            } catch (ConnectionFailedException e) {
                throw new RuntimeException(e);
            }
            assertThrows(DisconnectedException.class, () -> channel.read(new byte[1], 0, 1));
        }

        @Test
        void readingAfterSelfDisconnects() throws InterruptedException {
            var sender = new Task(server, () -> {
                try {
                    var channel = client.accept(666);
                    channel.write(new byte[] {1}, 0, 1);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            sender.start();

            Channel channel;
            try {
                channel = server.connect("client", 666);
            } catch (ConnectionFailedException e) {
                throw new RuntimeException(e);
            }

            Thread.sleep(150);

            channel.disconnect();
            assertThrows(DisconnectedException.class, () -> channel.read(new byte[1], 0, 1));
        }
    }
}
