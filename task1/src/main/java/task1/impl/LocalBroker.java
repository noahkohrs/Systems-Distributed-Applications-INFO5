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

    Semaphore host = new Semaphore(1, true);
    Semaphore connector = new Semaphore(0, true);

    public LocalBroker(String name) {
        super(name);
        BrokerManager.addBroker(this);
        // As we use synchronisation on the map, it's really important that the kind of map is not synchronized by default.
        rdvs = new HashMap<>();
    }

    @Override
    public Channel accept(int port) throws ConnectionFailedException {
        if (rdvs.containsKey(port) && rdvs.get(port).isWaitingForConnect()) {
            throw new ConnectionFailedException(ConnectionFailedException.Issue.PORT_IN_USE_FOR_THIS_BROKER, String.valueOf(port));
        }
        try {
            host.acquire();
            RendezVous rendezVous;
            rdvs.put(port, new RendezVous());
            rendezVous = rdvs.get(port);

            rendezVous.accept(this);
            connector.release();

            return rendezVous.getChannelForAcceptor();
        } catch (InterruptedException e) {
            throw new ConnectionFailedException(ConnectionFailedException.Issue.ERROR, "Interrupted");
        } finally {
            rdvs.remove(port);
            host.release();
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
            hostBroker.connector.acquire();
            RendezVous rendezVous = hostBroker.rdvs.get(port);
            rendezVous.connect(this);
            return rendezVous.getChannelForConnector();
        } catch (InterruptedException e) {
            throw new ConnectionFailedException(ConnectionFailedException.Issue.ERROR, "Interruption");
        }
    }

    public void delete() {
        BrokerManager.removeBroker(this.name);
    }
}