package task4.impl;

import org.junit.jupiter.api.Test;
import task4.Broker;
import task4.Channel;
import utils.TitiTotoTesting;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
class LocalBrokerTest extends TitiTotoTesting {
    private static final int ATTEMPTS = 10;

    @Test
    void fewClientConnecting() throws InterruptedException {
        List<Channel> connectionChannels = new ArrayList<>();
        List<Channel> acceptChannels = new ArrayList<>();

        titiBr.bind(8080, new AcceptListener(acceptChannels));

        for (int i = 0; i < ATTEMPTS; i++) {
            Broker clientBroker = new LocalBroker("client" + i);
            clientBroker.connect("titi", 8080, new ConnectListener(connectionChannels));
        }

        Thread.sleep(100);

        assertEquals(ATTEMPTS, connectionChannels.size());
        assertEquals(ATTEMPTS, acceptChannels.size());
        for (Channel channel : connectionChannels) {
            assertEquals(channel.parentBroker, titiBr);
        }
        for (Channel channel : acceptChannels) {
            assertTrue(channel.parentBroker.name.startsWith("client"));
        }


        var allChannels  = new HashSet<Channel>();
        allChannels.addAll(connectionChannels);
        allChannels.addAll(acceptChannels);

        assertEquals(ATTEMPTS * 2, allChannels.size());
        allChannels.forEach(channels -> {
            for (Channel channel : allChannels) {
                assertFalse(channel.disconnected());
            }
        });


    }
}

class ConnectListener implements Broker.ConnectListener {

    private final List<Channel> connectionChannels;

    ConnectListener(List<Channel> connectionChannels) {
        this.connectionChannels = connectionChannels;
    }

    @Override
    public void connected(Channel channel) {
        connectionChannels.add(channel);
    }

    @Override
    public void refused() {
        System.out.println("Refused");
    }
}

class AcceptListener implements Broker.AcceptListener {

    private final List<Channel> acceptChannels;

    AcceptListener(List<Channel> acceptChannels) {
        this.acceptChannels = acceptChannels;
    }

    @Override
    public void accepted(Channel channel) {
        acceptChannels.add(channel);
    }
}