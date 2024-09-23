package task1.impl;

import task1.Broker;
import task1.Channel;

public class RendezVous {

    Broker acceptor;
    Broker connector;
    LocalChannel channelForAcceptor;
    LocalChannel channelForConnector;

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

    synchronized Channel connect(LocalBroker b) {
           connector = b;
           if (acceptor == null) {
               // Wait for the other broker to connect
               try {
                   wait();
               } catch (InterruptedException e) {
                   throw new RuntimeException(e);
               }
           }
           createChannels();
           notify();
           return channelForConnector;
    }

    synchronized Channel accept(LocalBroker b) {
            acceptor = b;
            if (connector == null) {
                // Wait for the other broker to connect
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            createChannels();
            notify();
            return channelForAcceptor;
    }
    boolean isWaitingForAccept() {
        return connector == null;
    }
    boolean isWaitingForConnect() {
        return acceptor == null;
    }
}
