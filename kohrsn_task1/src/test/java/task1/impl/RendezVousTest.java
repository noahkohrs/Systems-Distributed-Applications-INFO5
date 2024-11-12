package task1.impl;

import org.junit.jupiter.api.Test;
import task1.Task;

import static org.junit.jupiter.api.Assertions.fail;

public class RendezVousTest {

    @Test
    void connectBeforeAccept() {
        LocalBroker connector = new LocalBroker("connector");
        LocalBroker acceptor = new LocalBroker("acceptor");
        RendezVous rendezVous = new RendezVous();
        var task1 = new Task(connector, () -> {
            try {
                rendezVous.connect(acceptor);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        var task2 = new Task(acceptor, () -> {
            try {
                rendezVous.accept(connector);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        task1.start();
        task2.start();

        try {
            task1.join();
            task2.join();
        } catch (InterruptedException e) {
            fail("Interrupted with " + e.getStackTrace());
        }

        acceptor.delete();
        connector.delete();
    }

    @Test
    void acceptBeforeConnect() {
        LocalBroker connector = new LocalBroker("connector");
        LocalBroker acceptor = new LocalBroker("acceptor");
        RendezVous rendezVous = new RendezVous();
        var task1 = new Task(acceptor, () -> {
            try {
                rendezVous.accept(connector);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        var task2 = new Task(connector, () -> {
            try {
                rendezVous.connect(acceptor);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        task1.start();
        task2.start();

        try {
            task1.join();
            task2.join();
        } catch (InterruptedException e) {
            fail("Interrupted with " + e.getStackTrace());
        }

        acceptor.delete();
        connector.delete();
    }
}
