package task1.impl;

import task1.Broker;
import task1.Channel;

import java.util.concurrent.Semaphore;

class RendezVous {

    Broker acceptor;
    Broker connector;
    LocalChannel channelForAcceptor;
    LocalChannel channelForConnector;
    Semaphore accept = new Semaphore(0, true);
    Semaphore connect = new Semaphore(0, true);

    private synchronized void createChannels() {
        if (channelForConnector == null && channelForAcceptor == null) {
            // Create a linked pair of channels
            channelForConnector = new LocalChannel(connector.name);
            channelForAcceptor = new LocalChannel(acceptor.name);

            // Link the channels to each other
            channelForConnector.oppositeGateway = channelForAcceptor;
            channelForAcceptor.oppositeGateway = channelForConnector;
        }
    }

    void connect(LocalBroker b) {
        if (connector != null) {
            throw new IllegalStateException("connector is already set");
        }
       connector = b;
    }

    Channel getChannelForConnector() throws InterruptedException {
        accept.release();
        connect.acquire();
        createChannels();

        return channelForConnector;
    }

    void accept(LocalBroker b) {
        if (acceptor != null) {
            throw new IllegalStateException("acceptor is already set");
        }
        acceptor = b;


    }

    Channel getChannelForAcceptor() throws InterruptedException {
        connect.release();
        accept.acquire();
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
