package task5.impl;

import task4.Channel;
import task5.Message;
import task5.MessageQueue;

/**
 * An implementation of the {@link MessageQueue} abstract working on top of a {@link Channel}.
 */
public final class MessageQueueImpl extends MessageQueue {

    private final Channel channel;

    private final ReaderAutomata readerAutomata;
    private final WriterAutomata writerAutomata;

    MessageQueueImpl(Channel channel) {
        super();
        this.channel = channel;
        readerAutomata = new ReaderAutomata(this);
        writerAutomata = new WriterAutomata(this, channel);
        channel.setListener(readerAutomata);
    }

    @Override
    public void send(Message message, WriteListener listener) {
        writerAutomata.send(message, listener);
    }

    @Override
    public void setListener(ReadListener listener) {
        readerAutomata.setListener(listener);
    }

    @Override
    public void close() {
        channel.disconnect();
    }
}
