import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import task5.MessageQueue;
import task5.QueueBroker;

public class EventQueueConnectionFailureTest {

    @Test
    public void testConnectionFailure() {

        Brokers.localQueueBroker.bind(1234, queue -> {});

        // Attempt to connect to a non-existent host
        Brokers.localQueueBroker.connect("NonExistingHost", 1234,
            new QueueBroker.ConnectListener() {
                @Override
                public void connected(MessageQueue queue) {
                    Assertions.fail("Connection should not succeed.");
                }

                @Override
                public void refused() {
                    // This is expected since connection should fail
                    Assertions.assertTrue(true);
                }
            }
        );

        // Wait a bit for connection attempt
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Brokers.localQueueBroker.unbind(1234);
    }
}
