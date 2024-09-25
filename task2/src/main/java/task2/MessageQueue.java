package task2;

public abstract class MessageQueue {

    /**
     * Send the given bytes to the message queue.
     * <br>
     * This method is blocking until all the bytes are sent.
     *
     * @param bytes the bytes to send.
     * @param offset the offset in the byte array.
     * @param length the number of bytes to send.
     */
    abstract void send(byte[] bytes, int offset, int length);

    /**
     * Receive bytes from the message queue.
     * <br>
     * This method is blocking until an entire message is received.
     * @return the received bytes.
     */
    abstract byte[] receive();

    /**
     * Close the message queue.
     * <br>
     * This method should be called when the message queue is no longer needed.
     * Has no effect if the message queue is already closed.
     */
    abstract void close();

    /**
     * Check if the message queue is closed.
     * @return true if the message queue is closed, false otherwise.
     */
    abstract boolean closed();
}
