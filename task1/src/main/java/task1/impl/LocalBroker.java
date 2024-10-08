package task1.impl;

import task1.Broker;
import task1.Channel;
import task1.exceptions.ConnectionFailedException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class LocalBroker extends Broker {

    /**
     * The list of rendez-vous points linked to ports.
     */
    final Map<Integer, RendezVous> rdvs;

    public LocalBroker(String name) {
        super(name);
        BrokerManager.addBroker(this);
        // As we use synchronisation on the map, it's not really important that the kind of map is not synchronized by default.
        rdvs = new HashMap<>();
    }

    @Override
    public Channel accept(int port) throws ConnectionFailedException {
        if (rdvs.containsKey(port) && rdvs.get(port).isWaitingForConnect()) {
            throw new ConnectionFailedException(ConnectionFailedException.Issue.PORT_IN_USE_FOR_THIS_BROKER, String.valueOf(port));
        }
        try {
            RendezVous rendezVous;
            synchronized (rdvs) {
                rendezVous = new RendezVous();
                rdvs.put(port, rendezVous);
                rdvs.notify();
            }

            return rendezVous.accept(this);
        } catch (InterruptedException e) {
            rdvs.remove(port);
            throw new ConnectionFailedException(ConnectionFailedException.Issue.ERROR, "Interrupted");
        }
    }

    @Override
    public Channel connect(String host, int port) throws ConnectionFailedException {
        LocalBroker hostBroker;
        try {
            var broker = BrokerManager.getBroker(host);
            if (!(broker instanceof LocalBroker)) {
                throw new IllegalArgumentException();
            }
            hostBroker = (LocalBroker) broker;
        } catch (IllegalArgumentException e) {
            throw new ConnectionFailedException(ConnectionFailedException.Issue.NO_BROKER_WITH_NAME, host);
        }
        try {
            RendezVous rendezVous;
            synchronized (hostBroker.rdvs) {
                while (!hostBroker.rdvs.containsKey(port)) {
                    hostBroker.rdvs.wait();
                }
                rendezVous = hostBroker.rdvs.remove(port);
            }
            return rendezVous.connect(this);
        } catch (InterruptedException e) {
            throw new ConnectionFailedException(ConnectionFailedException.Issue.ERROR, "Interruption");
        }
    }

    public void delete() {
        BrokerManager.removeBroker(this.name);
    }
}