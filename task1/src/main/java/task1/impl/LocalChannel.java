package task1.impl;

import info5.sar.utils.CircularBuffer;
import task1.Channel;
import task1.exceptions.DisconnectedException;

public class LocalChannel extends Channel {
    /**
     * Whether the channel is connected.
     */
    boolean connected = true;

    /**
     * The opposite channel side.
     */
    LocalChannel oppositeGateway;
    /**
     * The name of the broker that this channel side is associated with.
     * This is used for debugging purposes.
     */
    final String brokerName;

    CircularBuffer data = new CircularBuffer(1024);

    public LocalChannel(String brokerName) {
        super();
        this.brokerName = brokerName;
    }

    @Override
    public synchronized int read(byte[] bytes, int offset, int length) throws DisconnectedException {
        if (!this.connected) {
            throw new DisconnectedException(DisconnectedException.DisconnectionKind.NATURAL);
        }
        while (data.empty()) {
            if (!this.connected || !oppositeGateway.connected) {
                throw new DisconnectedException(DisconnectedException.DisconnectionKind.NATURAL);
            } else {
                try {
                    wait();
                } catch (InterruptedException e) {
                    disconnect();
                    throw new DisconnectedException(DisconnectedException.DisconnectionKind.ERROR);
                }
            }
        }
        var bytesRead = 0;
        while (!data.empty() && bytesRead < length) {
            bytes[offset + bytesRead] = data.pull();
            bytesRead++;
        }
        synchronized (oppositeGateway) {
            oppositeGateway.notifyAll();  // Notify writers waiting for space
        }
        return bytesRead; // Return the number of bytes read
    }

    @Override
    public int write(byte[] bytes, int offset, int length) throws DisconnectedException {
        if (!this.connected || !oppositeGateway.connected){
            throw new DisconnectedException(DisconnectedException.DisconnectionKind.NATURAL);
        }
        var data = oppositeGateway.data;
        while (data.full()) {
            try {
                if (!this.connected || !oppositeGateway.connected){
                    throw new DisconnectedException(DisconnectedException.DisconnectionKind.NATURAL);
                }
                wait();
            } catch (InterruptedException e) {
                disconnect();
                throw new DisconnectedException(DisconnectedException.DisconnectionKind.ERROR);
            }
        }
        var spaceToWrite = 0;
        while (!data.full() && spaceToWrite < length) {
            data.push(bytes[offset + spaceToWrite]);
            spaceToWrite++;
        }
        synchronized (oppositeGateway) {
            oppositeGateway.notifyAll();  // Notify readers waiting for data
        }
        return spaceToWrite; // Return the number of bytes written
    }


    @Override
    public synchronized void disconnect() {
        if (!connected) {
            return;
        }
        connected = false;
        notifyAll();
        synchronized (oppositeGateway) {
            oppositeGateway.notifyAll();
        }
    }

    @Override
    public boolean disconnected() {
        return !connected || !oppositeGateway.connected;
    }
}
