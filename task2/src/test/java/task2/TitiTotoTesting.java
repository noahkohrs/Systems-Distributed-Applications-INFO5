package task2;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import task1.impl.LocalBroker;
import task2.impl.LocalQueueBroker;

public class TitiTotoTesting {
    private LocalBroker titiBr;
    private LocalBroker totoBr;
    QueueBroker titi;
    QueueBroker toto;

    @BeforeEach
    void setUp() {
        titiBr = new LocalBroker("titi");
        totoBr = new LocalBroker("toto");
        titi = new LocalQueueBroker(titiBr);
        toto = new LocalQueueBroker(totoBr);
    }

    @AfterEach
    void tearDown() {
        titiBr.delete();
        totoBr.delete();
    }
}
