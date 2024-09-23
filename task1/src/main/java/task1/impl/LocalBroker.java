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
            throw new ConnectionFailedException(name);
        }
        rdvs.putIfAbsent(port, new RendezVous());
        RendezVous rendezVous = rdvs.get(port);
        Channel c = rendezVous.accept(this);
        rdvs.remove(port);
        notify();
        return c;
    }

    @Override
    public synchronized Channel connect(String host, int port) throws ConnectionFailedException {
        LocalBroker oppositeBroker = BrokerManager.getBroker(host);
        Map<Integer, RendezVous> oppositeBrokerRdvs = oppositeBroker.rdvs;
        try {
            // If there's already a rendezvous on the same port, wait for it to complete
            RendezVous rendezVous = oppositeBrokerRdvs.get(port);
            while (rendezVous != null && rendezVous.isWaitingForConnect()) {
                // Wait for the current connection attempt to complete
                try {
                    oppositeBroker.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            oppositeBrokerRdvs.putIfAbsent(port, new RendezVous());

            return rendezVous.connect(this);
        } catch (IllegalArgumentException e) {
            throw new ConnectionFailedException(host);
        }
    }
}