package task1.impl;

import task1.Broker;
import task1.Channel;

class RendezVous {

    Broker acceptor;
    Broker connector;
    LocalChannel channelForAcceptor;
    LocalChannel channelForConnector;

    private synchronized void createChannels() {
        if (channelForConnector == null && channelForAcceptor == null) {
            channelForConnector = new LocalChannel(connector);
            channelForAcceptor = new LocalChannel(acceptor);

            channelForConnector.oppositeGateway = channelForAcceptor;
            channelForAcceptor.oppositeGateway = channelForConnector;
        }
    }

    synchronized Channel connect(LocalBroker b) throws InterruptedException {
        if (connector != null) {
            throw new IllegalStateException("connector is already set");
        }
        connector = b;

        if (acceptor == null) {
            wait();
        } else {
            notify();
        }
        createChannels();

        return channelForConnector;
    }

    synchronized Channel accept(LocalBroker b) throws InterruptedException {
        if (acceptor != null) {
            throw new IllegalStateException("acceptor is already set");
        }
        acceptor = b;

        if (connector == null) {
            wait();
        } else {
            notify();
        }

        createChannels();
        return channelForAcceptor;
    }

    boolean isWaitingForAccept() {
        return acceptor == null;
    }

    boolean isWaitingForConnect() {
        return connector == null;
    }
}
