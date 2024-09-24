package task1.impl;

import org.junit.jupiter.api.RepeatedTest;
import task1.Task;
import task1.exceptions.ConnectionFailedException;

import static org.junit.jupiter.api.Assertions.fail;

public class LocalBrokerTest {
    // Debug purpose
    static LocalBroker connector;
    static LocalBroker acceptor;

    @RepeatedTest(100)
    void connectBeforeAccept() throws InterruptedException {
        connector = new LocalBroker("connector");
        acceptor = new LocalBroker("acceptor");

        var task1 = new Task(connector, () -> {
            try {
                connector.connect("acceptor", 0);
            } catch (ConnectionFailedException e) {
                fail("Connection failed with " + e.getStackTrace());
            }
        });

        var task2 = new Task(acceptor, () -> {
            try {
                acceptor.accept(0);
            } catch (ConnectionFailedException e) {
                fail("Connection failed with " + e.getStackTrace());
            }
        });

        task1.start();
        Thread.sleep(5);
        task2.start();

        try {
            task1.join();
            task2.join();
        } catch (InterruptedException e) {
            fail("Interrupted with " + e.getStackTrace());
        }
    }

    @RepeatedTest(100)
    void acceptBeforeConnect() throws InterruptedException {
        acceptor = new LocalBroker("acceptor");
        connector = new LocalBroker("connector");

        var task1 = new Task(acceptor, () -> {
            try {
                acceptor.accept(0);
            } catch (ConnectionFailedException e) {
                fail("Connection failed with " + e.getStackTrace());
            }
        });
        var task2 = new Task(connector, () -> {
            try {
                connector.connect("acceptor", 0);
            } catch (ConnectionFailedException e) {
                fail("Connection failed with " + e.getStackTrace());
            }
        });

        task1.start();
        Thread.sleep(5);
        task2.start();

        try {
            task1.join();
            task2.join();
        } catch (InterruptedException e) {
            fail("Interrupted with " + e.getStackTrace());
        }
    }
}
