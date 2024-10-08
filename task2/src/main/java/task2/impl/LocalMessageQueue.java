package task2.impl;

import task1.Channel;
import task1.exceptions.DisconnectedException;
import task2.MessageQueue;

import java.util.concurrent.Semaphore;

public class LocalMessageQueue extends MessageQueue {

    private final Channel channel;

    Semaphore sendMutex = new Semaphore(1, true);
    Semaphore receiveMutex = new Semaphore(1, true);

    public LocalMessageQueue(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void send(byte[] bytes, int offset, int length) throws DisconnectedException {
        try {
            sendMutex.acquire();

            var lengthBytes = intToBytes(length);

            var lengthIndex = 0;
            while (lengthIndex < 4) {
                lengthIndex += channel.write(lengthBytes, lengthIndex, 4 - lengthIndex);
            }

            var index = 0;
            while (index < length) {
                index += channel.write(bytes, offset + index, length - index);
            }

        } catch (InterruptedException e) {
            throw new DisconnectedException(DisconnectedException.DisconnectionKind.ERROR);
        } finally {
            sendMutex.release();
        }

    }

    @Override
    public byte[] receive() throws DisconnectedException {
        try {
            var lengthBytes = new byte[4];
            var lengthIndex = 0;
            receiveMutex.acquire();
            while (lengthIndex < 4) {
                lengthIndex += channel.read(lengthBytes, lengthIndex, 4);
            }
            var length = bytesToInt(lengthBytes);
            var bytes = new byte[length];
            var index = 0;
            while (index < length) {
                index += channel.read(bytes, index, length - index);
            }
            return bytes;
        } catch (InterruptedException e) {
            throw new DisconnectedException(DisconnectedException.DisconnectionKind.ERROR);
        } finally {
            receiveMutex.release();
        }
    }

    @Override
    public void close() {
        channel.disconnect();
    }

    @Override
    public boolean closed() {
        return channel.disconnected();
    }

    private static byte[] intToBytes(int value) {
        var bytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            bytes[i] = (byte) (value >> (i * 8));
        }
        return bytes;
    }

    private static int bytesToInt(byte[] bytes) {
        var value = 0;
        for (int i = 0; i < 4; i++) {
            value |= (bytes[i] & 0xFF) << (i * 8);
        }
        return value;
    }
}
