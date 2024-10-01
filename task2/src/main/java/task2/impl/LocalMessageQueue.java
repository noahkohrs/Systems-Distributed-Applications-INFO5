package task2.impl;

import task1.Channel;
import task1.exceptions.DisconnectedException;
import task2.MessageQueue;

public class LocalMessageQueue extends MessageQueue {

    private final Channel channel;

    public LocalMessageQueue(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void send(byte[] bytes, int offset, int length) throws DisconnectedException {

    }

    @Override
    public byte[] receive() throws DisconnectedException {
        return new byte[0];
    }

    @Override
    public void close() {

    }

    @Override
    public boolean closed() {
        return false;
    }
}
