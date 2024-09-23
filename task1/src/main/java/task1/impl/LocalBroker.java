package task1.impl;

import task1.Broker;
import task1.Channel;
import task1.exceptions.ConnectionFailedException;

import java.util.HashMap;
import java.util.Map;

public class LocalBroker extends Broker {

    /**
     * The list of rendez-vous points linked to ports.
     */
    final Map<Integer, RendezVous> rdvs;

    public LocalBroker(String name) {
        super(name);
        BrokerManager.addBroker(this);
        // As we use synchronisation on the map, it's really important that the kind of map is not synchronized by default.
        rdvs = new HashMap<>();
    }

    @Override
    public synchronized Channel accept(int port) throws ConnectionFailedException {
        if (rdvs.containsKey(port) && rdvs.get(port).isWaitingForAccept()) {
            throw new ConnectionFailedException(ConnectionFailedException.Issue.PORT_IN_USE_FOR_THIS_BROKER, String.valueOf(port));
        }
        rdvs.putIfAbsent(port, new RendezVous());
        RendezVous rendezVous = rdvs.get(port);
        Channel c = rendezVous.accept(this);
        rdvs.remove(port);
        // Notify all brokers that a connection has been established to wake up any threads waiting for a connection
        BrokerManager.brokers.forEach(b -> {
                    synchronized (b) {
                        b.notifyAll();
                    }
                });
        return c;
    }

    @Override
    public synchronized Channel connect(String host, int port) throws ConnectionFailedException {
        LocalBroker hostBroker;
        try {
            hostBroker = BrokerManager.getBroker(host);
        } catch (IllegalArgumentException e) {
            throw new ConnectionFailedException(ConnectionFailedException.Issue.NO_BROKER_WITH_NAME, host);
        }
        Map<Integer, RendezVous> oppositeBrokerRdvs = hostBroker.rdvs;
        // If there's already a connection attempt in progress, wait for it to complete
        RendezVous rendezVous = oppositeBrokerRdvs.get(port);
        while (rendezVous != null && rendezVous.isWaitingForConnect()) {
            // Wait for the current connection attempt to complete
            try {
                hostBroker.wait();
                rendezVous = oppositeBrokerRdvs.get(port);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        oppositeBrokerRdvs.putIfAbsent(port, new RendezVous());
        rendezVous = oppositeBrokerRdvs.get(port);

        return rendezVous.connect(this);
    }

    public void delete() {
        BrokerManager.removeBroker(this);
    }
}