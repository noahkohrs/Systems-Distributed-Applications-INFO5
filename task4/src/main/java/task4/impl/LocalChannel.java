package task4.impl;

import task4.Channel;
import task4.EventPump;
import task4.utils.CircularBuffer;

import java.util.Arrays;

public class LocalChannel extends Channel {

    boolean isAwareOfChanges = false;
    boolean connected = true;
    ReadListener readListener;
    private LocalChannel oppositeGateway;
    private final CircularBuffer data;

    LocalChannel(LocalBroker broker, int bufferSize) {
        super(broker);
        data = new CircularBuffer(bufferSize);
    }

    @Override
    public void setListener(ReadListener l) {
        readListener = l;
    }

    @Override
    public boolean write(byte[] bytes, int offset, int length, WriteListener listener) {
        if (!connected) {
            return false;
        }
        var start = offset;
        while (!oppositeGateway.data.full() && length > 0) {
            oppositeGateway.data.push(bytes[offset]);
            offset++;
            length--;
        }
        var end = offset;
        if (!oppositeGateway.isAwareOfChanges) {
            oppositeGateway.isAwareOfChanges = true;
            EventPump.post(new ReadEvent(oppositeGateway));
        }
        EventPump.post(new WriteEvent(Arrays.toString(Arrays.copyOfRange(bytes, start, end)), this, this.oppositeGateway, () -> listener.written(end - start)));

        return true;
    }

    @Override
    public int read(byte[] bytes, int offset, int length) {
        if (!connected) {
            return 0;
        }

        var bytesRead = 0;
        while (!data.empty() && bytesRead < length) {
            bytes[offset + bytesRead] = data.pull();
            bytesRead++;
        }
        return bytesRead;
    }

    @Override
    public boolean isEmpty() {
        return data.empty();
    }

    @Override
    public void disconnect() {
        EventPump.post(new ClosedEvent(this, this.oppositeGateway));
    }

    @Override
    public boolean disconnected() {
        return !connected;
    }

    public static void linkChannels(LocalChannel channel1, LocalChannel channel2) {
        channel1.oppositeGateway = channel2;
        channel2.oppositeGateway = channel1;
    }
}
