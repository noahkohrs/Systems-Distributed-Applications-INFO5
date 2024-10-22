package task4;

/**
 * A channel is a network abstraction that can read and write bytes.<br>
 * This is an abstract class that represents a channel.<br>
 * A Channel is FIFO (First In First Out) loss less.
 * <br>
 * A channel is full duplex, meaning that it can read and write at the same time.
 *
 * @see Broker
 */
public abstract class Channel {

    public final Broker parentBroker;

    public Channel(Broker b) {
        parentBroker = b;
    }

    /**
     * Listener interface to handle incoming messages and connection lifecycle events.
     */
    public interface ReadListener {
        /**
         * Called when a new message is received by this {@link Channel}.
         * The callback only happens once after any upcoming write operation
         * happened since last time this event has been received.
         * <br>
         * The function will never be executed concurrently with other events.
         *
         * @param channel the {@link Channel} that received the message.
         */
        void received(Channel channel);

        /**
         * Called when the {@link Channel} has been closed by the opposite side.
         * <br>
         * The function will never be executed concurrently with other events.
         */
        default void closed() {
        }
    }

    /**
     * Registers a {@link ReadListener} to receive notifications for incoming messages and closure events.
     * <br>
     * Only one listener can be registered at a time, subsequent calls to this method will replace the existing listener.
     *
     * @param l the listener to be registered
     */
    public abstract void setListener(ReadListener l);

    public interface WriteListener {
        /**
         * Called when the {@link Channel} has written the given number of bytes.
         * <br>
         * The function is a callback of the write method.
         * <br>
         * The function will never be executed concurrently with other events.
         */
        void written(int bytes);
    }

    /**
     * Write up to length bytes from the given buffer to the channel.
     * <br>
     * This is a non-blocking operation, it will return only once all the bytes have been written.
     * Note: Zero bytes can be written if the channel is full or the connection is closed.
     *
     * @param bytes  the buffer to write from.
     * @param offset the offset in the buffer to start writing from.
     * @param length the number of bytes to write.
     * @return true if the operation succeeded, false otherwise.
     */
    public abstract boolean write(byte[] bytes, int offset, int length, WriteListener listener);

    /**
     * Read up to length bytes from the channel into the given buffer.
     *
     * @param bytes  the buffer to read into.
     * @param offset the offset in the buffer to start reading into.
     * @param length the maximum number of bytes to read.
     * @return the number of bytes read.
     */
    public abstract int read(byte[] bytes, int offset, int length);

    /**
     * Disconnect the channel.
     * Has no effect if the channel is already disconnected.
     * <br>
     * Interrupts any ongoing read or write operations.
     * <br>
     * The opposite side of the channel will still be able to read any remaining data while the instance calling the function will not be able to read or write any more data.
     */
    public abstract void disconnect();

    /**
     * Check if the channel is disconnected.
     *
     * @return true if the channel is disconnected, false otherwise.
     */
    public abstract boolean disconnected();
}