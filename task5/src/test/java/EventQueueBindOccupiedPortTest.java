import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import task4.Broker;
import task4.impl.LocalBroker;
import task5.QueueBroker;
import task5.impl.QueueBrokerImpl;

public class EventQueueBindOccupiedPortTest {

    @Test
    public void testBindOccupiedPort() {
        // First bind attempt (this should work)
        Brokers.localQueueBroker.bind(1234, queue -> {});

        // Second bind attempt (should fail because the port is already bound)
        Assertions.assertThrows(IllegalStateException.class, () -> {
            Brokers.localQueueBroker.bind(1234, queue -> {});
        });
    }
}
