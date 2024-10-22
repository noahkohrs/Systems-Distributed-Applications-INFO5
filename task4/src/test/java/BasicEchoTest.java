import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import task4.Broker;
import task4.Channel;
import task4.impl.LocalBroker;

public class BasicEchoTest {

    public static final int PORT = 8080;

    @Test
    public void testBasicEcho() {
        byte[] bytesToSend = new byte[128];
        for (int i = 0; i < bytesToSend.length; i++) {
            bytesToSend[i] = (byte) i;
        }
        byte[] receivedBytes = new byte[bytesToSend.length];

        Broker broker = new LocalBroker("EchoServer");
        Broker clientBroker = new LocalBroker("EchoClient");

        broker.bind(PORT, new EchoServer());
        clientBroker.connect("EchoServer", PORT, new EchoClient(bytesToSend, receivedBytes));

        // Basic wait for the test to happen.
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        broker.unbind(PORT);

        for (int i = 0; i < bytesToSend.length; i++) {
            Assertions.assertEquals(bytesToSend[i], receivedBytes[i], """
                    
                    The received bytes should be the same as the sent bytes.
                    Array sent:     %d
                    Array received: %d
                    Array index:    %d
                    """.formatted(bytesToSend[i], receivedBytes[i], i));
        }
    }
}


class EchoServer implements Broker.AcceptListener {

    @Override
    public void accepted(Channel channel) {
        channel.setListener(new EchoListener());
    }
}

class EchoListener implements Channel.ReadListener {

    @Override
    public void received(Channel channel) {
        byte[] buffer = new byte[16];
        do {
            int bytesRead = channel.read(buffer, 0, buffer.length);
            if (bytesRead == 0) {
                break;
            }
            channel.write(buffer, 0, bytesRead, bytes -> System.out.println("Echoed " + bytes + " bytes."));
        } while (true);
    }

    @Override
    public void closed() {
        System.out.println("Unexpected Channel closed.");
    }
}

class EchoClient implements Broker.ConnectListener {

    private final byte[] bytesToSend;
    private final byte[] receivedBytes;

    EchoClient(byte[] bytesToSend, byte[] receivedBytes) {
        this.bytesToSend = bytesToSend;
        this.receivedBytes = receivedBytes;
    }

    @Override
    public void connected(Channel channel) {
        for (int i = 0; i < bytesToSend.length; i++) {
            int finalI = i;
            channel.write(bytesToSend, i, 1, bytes -> channel.read(receivedBytes, finalI, 1));
        }
        channel.setListener(new ResultForwarder(receivedBytes));
    }

    @Override
    public void refused() {
        System.out.println("Connection refused.");
    }
}

class ResultForwarder implements Channel.ReadListener {

    private final byte[] arrival;
    private int idx = 0;

    ResultForwarder(byte[] arrival) {
        this.arrival = arrival;
    }

    @Override
    public void received(Channel channel) {
        int length = channel.read(arrival, idx, arrival.length - idx);
        idx += length;
    }
}