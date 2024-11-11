package task5.impl;

import task4.Channel;
import task5.Message;
import task5.MessageQueue;

import java.util.LinkedList;
import java.util.Queue;

public class WriterAutomata implements Channel.WriteListener {

    private final MessageQueue owner;
    private final Channel channel;
    private final Queue<ToSendMessage> toSend = new LinkedList<>();
    private byte[] currentBytes;
    private int currentBytesIndex = 0;
    private MessageQueue.WriteListener currentMessageListener = null;
    private State state = State.WAITING_FOR_NEXT;

    public WriterAutomata(MessageQueue owner, Channel channel) {
        this.owner = owner;
        this.channel = channel;
    }

    boolean send(Message message, MessageQueue.WriteListener listener) {
        toSend.add(new ToSendMessage(message, listener));
        if (state == State.WAITING_FOR_NEXT) {
            sendWhateverICan();
        }
        return true;
    }

    private void sendWhateverICan() {
        if (state == State.WAITING_FOR_NEXT && !toSend.isEmpty()) {
            var toSendMsg = toSend.poll();
            if (toSendMsg != null) {
                var message = toSendMsg.message;
                var length = message.getLength();
                currentBytes = new byte[length + 4];
                System.arraycopy(intToBytes(length), 0, currentBytes, 0, 4);
                System.arraycopy(message.getBytes(), 0, currentBytes, 4, length);
                currentBytesIndex = 0; // Reset the index for new message
                currentMessageListener = toSendMsg.listener;
                state = State.SENDING_BYTES;
            }
        }

        if (state == State.SENDING_BYTES) {
            int remaining = currentBytes.length - currentBytesIndex;
            channel.write(currentBytes, currentBytesIndex, remaining, this);
        }
    }

    @Override
    public void written(int bytes) {
        currentBytesIndex += bytes;

        if (currentBytesIndex == currentBytes.length) {
            state = State.WAITING_FOR_NEXT;
            currentBytesIndex = 0;
            if (currentMessageListener != null) {
                Message createdMessage = new Message(currentBytes, 4, currentBytes.length - 4);
                currentMessageListener.written(createdMessage, owner);
            }
        } else if (currentBytesIndex > currentBytes.length) {
            throw new IllegalStateException("PANIC: Wrote more bytes than expected");
        }
        sendWhateverICan();
    }


    private static byte[] intToBytes(int value) {
        byte[] bytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            bytes[i] = (byte) (value >> (i * 8));
        }
        return bytes;
    }

    enum State {
        WAITING_FOR_NEXT,
        SENDING_BYTES,
    }

    private static class ToSendMessage {
        final Message message;
        final MessageQueue.WriteListener listener;

        ToSendMessage(Message message, MessageQueue.WriteListener listener) {
            this.message = message;
            this.listener = listener;
        }
    }
}
