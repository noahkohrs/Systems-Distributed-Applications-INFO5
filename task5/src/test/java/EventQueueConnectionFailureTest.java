import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import task4.Broker;
import task4.impl.LocalBroker;
import task5.Message;
import task5.MessageQueue;
import task5.QueueBroker;
import task5.impl.QueueBrokerImpl;

import java.util.ArrayList;

public class EventQueueConnectionFailureTest {

    @Test
    public void testConnectionFailure() {
        Broker localBroker = new LocalBroker("LocalBroker");
        QueueBroker localQueueBroker = new QueueBrokerImpl(localBroker);

        localQueueBroker.bind(1234, queue -> {});

        // Attempt to connect to a non-existent host
        localQueueBroker.connect("NonExistingHost", 1234,
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
    }
}
