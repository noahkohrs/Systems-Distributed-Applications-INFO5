package task1.impl;

import org.apache.commons.io.input.buffer.CircularByteBuffer;
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

    CircularByteBuffer data = new CircularByteBuffer(1024);

    public LocalChannel(String brokerName) {
        super();
        this.brokerName = brokerName;
    }

    @Override
    public synchronized int read(byte[] bytes, int offset, int length) throws DisconnectedException {
        while (data.getCurrentNumberOfBytes() == 0) {
            if (!connected) {
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
        var bytesRead = Math.min(data.getCurrentNumberOfBytes(), length);
        data.read(bytes, offset, bytesRead);
        synchronized (oppositeGateway) {
            oppositeGateway.notifyAll();  // Notify writers waiting for space
        }
        return bytesRead; // Return the number of bytes read
    }

    @Override
    public int write(byte[] bytes, int offset, int length) throws DisconnectedException {
        if (!connected) {
            throw new DisconnectedException(DisconnectedException.DisconnectionKind.NATURAL);
        }
        var data = oppositeGateway.data;
        while (data.getSpace() == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                disconnect();
                throw new DisconnectedException(DisconnectedException.DisconnectionKind.ERROR);
            }
        }
        var spaceToWrite = Math.min(data.getSpace(), length);
        data.add(bytes, offset, spaceToWrite);
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
        oppositeGateway.connected = false;
        notifyAll();
        synchronized (oppositeGateway) {
            oppositeGateway.notifyAll();
        }
    }

    @Override
    public boolean disconnected() {
        return !connected;
    }
}
