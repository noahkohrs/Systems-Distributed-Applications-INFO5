package task2;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import task1.impl.LocalBroker;
import task2.impl.LocalQueueBroker;

public class TitiTotoTesting {
    private static LocalBroker titiBr;
    private static LocalBroker totoBr;
    static QueueBroker titi;
    static QueueBroker toto;

    @BeforeAll
    static void setUp() {
        titiBr = new LocalBroker("titi");
        totoBr = new LocalBroker("toto");
        titi = new LocalQueueBroker(titiBr);
        toto = new LocalQueueBroker(totoBr);
    }

    @AfterAll
    static void tearDown() {
        titiBr.delete();
        totoBr.delete();
    }
}
