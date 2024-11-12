package task2;

import org.junit.jupiter.api.Test;
import task1.exceptions.ConnectionFailedException;

import static org.junit.jupiter.api.Assertions.*;
class QueueBrokerTest extends TitiTotoTesting {

    @Test
    void connectBeforeAccept() {
        new Task(titi, () -> {
            try {
                titi.connect("toto", 0);
            } catch (ConnectionFailedException e) {
                fail("Brokers exists, the connection should be successful");
            }
        }).start();

        try {
            toto.accept(0);
        } catch (ConnectionFailedException e) {
            fail("Brokers exists, the connection should be successful");
        }
    }

    @Test
    void acceptBeforeConnect() {
        new Task(toto, () -> {
            try {
                toto.accept(0);
            } catch (ConnectionFailedException e) {
                fail("Brokers exists, the connection should be successful");
            }
        }).start();

        try {
            titi.connect("toto", 0);
        } catch (ConnectionFailedException e) {
            fail("Brokers exists, the connection should be successful");
        }
    }

    @Test
    void acceptAndConnectFromTheSameBroker() {
        new Task(titi, () -> {
            try {
                titi.accept(0);
            } catch (ConnectionFailedException e) {
                fail("Brokers exists, the connection should be successful");
            }
        }).start();
        try {
            titi.connect("titi", 0);
        } catch (ConnectionFailedException e) {
            fail("Brokers exists, the connection should be successful");
        }
    }
}