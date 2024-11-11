package task5;

import com.sun.nio.sctp.MessageInfo;
import task4.Broker;
import task4.Channel;

/**
 * A MessagesQueue represents a channel for sending and receiving {@link Message} objects.
 * <br>
 * Unlike a {@link task4.Channel}, this abstraction works at the message level instead of individual bytes.
 */
public abstract class MessageQueue {

    protected final Channel channel;

    protected MessageQueue(Channel channel) {
        this.channel = channel;
    }

    /**
     * Sends a {@link Message} to the remote peer.
     * <br>
     * This is a non-blocking operation that will return immediately, triggering the {@link WriteListener#written(Message, MessageQueue)} once the message is fully sent.
     *
     * @param message the message to be sent.
     * @param listener the listener to handle the completion of the send operation.
     * @throws IllegalStateException if the connection is closed or an error occurs while sending.
     */
    public abstract void send(Message message, WriteListener listener);

    /**
     * Registers a {@link ReadListener} to handle incoming messages and connection lifecycle events.
     * <br>
     * Only one listener can be registered at a time. Calling this method again will replace the existing listener.
     *
     * @param listener the listener to handle received messages.
     */
    public abstract void setListener(ReadListener listener);

    /**
     * Closes the connection and stops message exchange.
     * <br>
     * After calling this method, no further messages can be sent or received.
     */
    public abstract void close();

    /**
     * Listener interface to handle incoming messages and connection lifecycle events.
     */
    public interface ReadListener {
        /**
         * Called when a new {@link Message} is received.
         *
         * @param message the received message.
         * @param queue the {@link MessageQueue} that received the message.
         */
        void received(Message message, MessageQueue queue);

        /**
         * Called when the connection has been closed by the remote peer.
         */
        void closed();
    }

    /**
     * Listener interface to handle the completion of message sending operations.
     */
    public interface WriteListener {
        /**
         * Called when the {@link Message} has been successfully sent.
         *
         * @param message the message that was sent.
         * @param queue the {@link MessageQueue} that sent the message.
         */
        void written(Message message, MessageQueue queue);
    }
}
