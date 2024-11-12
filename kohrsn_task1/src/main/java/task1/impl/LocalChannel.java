package task1.impl;

import info5.sar.utils.CircularBuffer;
import task1.Broker;
import task1.Channel;
import task1.exceptions.DisconnectedException;

public class LocalChannel extends Channel {
    /**
     * Whether the channel is connected.
     */
    volatile boolean connected = true;

    /**
     * The opposite channel side.
     */
    protected LocalChannel oppositeGateway;

    private final CircularBuffer data;

    public LocalChannel(Broker broker) {
        this(broker, 1024);
    }

    public LocalChannel(Broker broker, int bufferSize) {
        super(broker);
        data = new CircularBuffer(bufferSize);
    }

    @Override
    public int read(byte[] bytes, int offset, int length) throws DisconnectedException {
        synchronized (this) {
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
            notifyAll();
            return bytesRead; // Return the number of bytes read
        }
    }

    @Override
    public int write(byte[] bytes, int offset, int length) throws DisconnectedException {
        synchronized (oppositeGateway) {
            try {

                if (!this.connected || !oppositeGateway.connected) {
                    throw new DisconnectedException(DisconnectedException.DisconnectionKind.NATURAL);
                }
                var data = oppositeGateway.data;
                while (data.full()) {
                    try {
                        if (!this.connected || !oppositeGateway.connected) {
                            throw new DisconnectedException(DisconnectedException.DisconnectionKind.NATURAL);
                        }
                        oppositeGateway.wait();
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
                return spaceToWrite;
            } finally {
                oppositeGateway.notify();
            }
        }
    }

    @Override
    public void disconnect() {
        synchronized (this) {
            if (!connected) {
                return;
            }
            connected = false;
            notifyAll();
        }
        synchronized (oppositeGateway) {
            oppositeGateway.notifyAll();
        }
    }

    @Override
    public boolean disconnected() {
        return !connected || !oppositeGateway.connected;
    }
}
