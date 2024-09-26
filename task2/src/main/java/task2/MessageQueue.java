package task2;

import task1.exceptions.DisconnectedException;

/**
 * A {@link Task} that can send and receive bytes.
 * <br>
 * It is thread safe and full duplex while respecting a FIFO ordering.
 * <br>
 * A message queue is alive until an error occurs, or it is closed via {@link #close()}.
 */
public abstract class MessageQueue {

    /**
     * Send the given bytes (called message) to the other end of the message queue, which can be received by calling {@link #receive()}.
     * <br>
     * This method is blocking until all the bytes are sent. If the connection is closed while sending, a {@link DisconnectedException} is thrown.
     *
     * @param bytes  the bytes to send.
     * @param offset the offset in the byte array.
     * @param length the number of bytes to send.
     * @throws DisconnectedException if the message queue is disconnected.
     */
    public abstract void send(byte[] bytes, int offset, int length) throws DisconnectedException;

    /**
     * Receive bytes from the message queue.
     * <br>
     * This method is blocking until a message is received, which can be sent by calling {@link #send(byte[], int, int)} on the other end.
     * <br>
     * The message reading respect a FIFO order. if the connection is closed while reading, a {@link DisconnectedException} is thrown.
     *
     * @return the received bytes.
     * @throws DisconnectedException if the message queue is disconnected.
     */
    public abstract byte[] receive() throws DisconnectedException;

    /**
     * Close the message queue.
     * <br>
     * This method should be called when the message queue is no longer needed.
     * Has no effect if the message queue is already closed.
     */
    public abstract void close();

    /**
     * Check if the message queue is closed.
     *
     * @return true if the message queue is closed, false otherwise.
     */
    public abstract boolean closed();
}
