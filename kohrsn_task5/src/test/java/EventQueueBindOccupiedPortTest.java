import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EventQueueBindOccupiedPortTest {

    @Test
    public void testBindOccupiedPort() {
        // First bind attempt (this should work)
        Brokers.localQueueBroker.bind(1234, queue -> {});

        // Second bind attempt (should fail because the port is already bound)
        Assertions.assertThrows(IllegalStateException.class, () -> {
            Brokers.localQueueBroker.bind(1234, queue -> {});
        });

        // Unbind the port
        Brokers.localQueueBroker.unbind(1234);
    }
}
