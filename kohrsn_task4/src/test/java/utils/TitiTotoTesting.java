package utils;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import task4.impl.LocalBroker;

public class TitiTotoTesting {
    protected static LocalBroker titiBr;
    protected static LocalBroker totoBr;

    @BeforeAll
    static void setUp() {
        titiBr = new LocalBroker("titi");
        totoBr = new LocalBroker("toto");
    }

    @AfterAll
    static void tearDown() {
        titiBr.delete();
        totoBr.delete();
    }
}

