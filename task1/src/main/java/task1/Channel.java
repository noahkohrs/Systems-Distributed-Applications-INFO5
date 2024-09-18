package task1;

import task1.exceptions.DisconnectedException;

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

    /**
     * Read up to length bytes from the channel into the given buffer.
     * <br>
     * This is a blocking operation, it will return only once at least one byte has been read.
     *
     * @param bytes the buffer to read into.
     * @param offset the offset in the buffer to start reading into.
     * @param length the maximum number of bytes to read.
     * @return the number of bytes read.
     * @throws DisconnectedException if the channel is disconnected and there is no more bytes to read.
     */
    public abstract int read(byte[] bytes, int offset, int length) throws DisconnectedException;

    /**
     * Write up to length bytes from the given buffer to the channel.
     * <br>
     * This is a blocking operation, it will return only once all the bytes have been written.
     *
     * @param bytes the buffer to write from.
     * @param offset the offset in the buffer to start writing from.
     * @param length the number of bytes to write.
     * @return the number of bytes written once it succeed.
     * @throws DisconnectedException if the channel is disconnected.
     */
    public abstract int write(byte[] bytes, int offset, int length) throws DisconnectedException;

    /**
     * Disconnect the channel.
     * Has no effect if the channel is already disconnected.
     *
     * Only takes effect after all the pending read and write operations have completed.
     *
     */
    public abstract void disconnect();

    /**
     * Check if the channel is disconnected.
     * @return true if the channel is disconnected, false otherwise.
     */
    public abstract boolean disconnected();
}

