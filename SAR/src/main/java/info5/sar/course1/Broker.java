package info5.sar.course1;

abstract class Broker {

    /**
     * Create a new broker with the given name.
     * A broker is a network abstraction that can accept incoming connections on a given port and connect to hosts.
     *
     * @param name the name of the broker
     */
    Broker(String name) {
        throw new UnsupportedOperationException("Should be overridden");
    }

    /**
     * Accept an incoming connection on the given port.
     *
     * @param port the port to listen on
     * @return a channel representing the connection
     */
    abstract Channel accept(int port);

    /**
     * Connect to a remote host on the given port.
     *
     * @param host the host to connect to
     * @param port the port to connect to
     * @return a channel representing the connection
     */
    abstract Channel connect(String host, int port);
}

abstract class Channel {

    /**
     * Read up to length bytes from the channel into the given buffer.
     * This is a blocking operation, it will return only once at least one byte has been read.
     *
     * @param bytes the buffer to read into.
     * @param offset the offset in the buffer to start reading into.
     * @param length the maximum number of bytes to read.
     * @return the number of bytes read, or -1 if the channel is disconnected.
     */
    abstract int read(byte[] bytes, int offset, int length);

    /**
     * Write up to length bytes from the given buffer to the channel.
     *
     * This is a blocking operation, it will return only once all the bytes have been written.
     *
     * @param bytes the buffer to write from.
     * @param offset the offset in the buffer to start writing from.
     * @param length the number of bytes to write.
     * @return the number of bytes written once it succeed and -1 if the channel is disconnected.
     */
    abstract int write(byte[] bytes, int offset, int length);

    /**
     * Disconnect the channel.
     * Has no effect if the channel is already disconnected.
     */
    abstract void disconnect();

    /**
     * Check if the channel is disconnected.
     * @return true if the channel is disconnected, false otherwise.
     */
    abstract boolean disconnected();
}

