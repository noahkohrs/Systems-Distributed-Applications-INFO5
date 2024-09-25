package task1.impl;

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

        @Test
        void disconnectWhileBufferStillHasToRead() {
            var server = new LocalBroker("server");
            var client = new LocalBroker("client");
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
                assertEquals(1, channel.read(new byte[1], 0, 1));
            } catch (DisconnectedException e) {
                throw new RuntimeException(e);
            }

            assertThrows(DisconnectedException.class, () -> channel.read(new byte[1], 0, 1));

            server.delete();
            client.delete();
        }

        @Test
        void disconnectedWhileReadWaiting() {
            var server = new LocalBroker("server");
            var client = new LocalBroker("client");
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

            server.delete();
            client.delete();
        }
    }
}
