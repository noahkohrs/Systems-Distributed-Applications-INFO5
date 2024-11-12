package task5.impl;

import task4.Channel;
import task5.Message;
import task5.MessageQueue;

class ReaderAutomata implements Channel.ReadListener {

    private final MessageQueue owner;

    private MessageQueue.ReadListener listener;
    State state = State.READ_LENGTH;
    int currentLength = 0;
    int currentIndex = 0;
    byte[] lengthBuffer = new byte[4];
    byte[] messageBuffer;

    ReaderAutomata(MessageQueue owner) {
        this.owner = owner;
    }

    void setListener(MessageQueue.ReadListener listener) {
        this.listener = listener;
    }

    @Override
    public void received(Channel channel) {
        while (!channel.isEmpty()) {
            if (state == State.READ_LENGTH) {
                currentIndex += channel.read(lengthBuffer, currentIndex, 4 - currentIndex);
                if (currentIndex == 4) {
                    currentLength = bytesToInt(lengthBuffer);
                    messageBuffer = new byte[currentLength];
                    currentIndex = 0;
                    state = State.READ_CONTENT;
                }
            } else if (state == State.READ_CONTENT) {
                currentIndex += channel.read(messageBuffer, currentIndex, currentLength - currentIndex);
                if (currentIndex == currentLength) {
                    if (listener != null)
                        listener.received(new Message(messageBuffer), owner);
                    state = State.READ_LENGTH;
                    currentIndex = 0;
                }
            }
        }
    }

    @Override
    public void closed() {
        if (listener != null) {
            listener.closed();
        }
    }

    private static int bytesToInt(byte[] bytes) {
        var value = 0;
        for (int i = 0; i < 4; i++) {
            value |= (bytes[i] & 0xFF) << (i * 8);
        }
        return value;
    }
}
enum State {
    READ_LENGTH,
    READ_CONTENT
}