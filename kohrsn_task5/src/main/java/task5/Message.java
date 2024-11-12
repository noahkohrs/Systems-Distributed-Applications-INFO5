package task5;

/**
 * A message is a byte array that can be sent through a {@link MessageQueue}.
 * <br>
 * Warning: the {@code bytes} array is the only field which will be written when sent to the message queue.
 */
public class Message {

    /**
     * The message content.
     */
    protected byte[] bytes;

    /**
     * Constructs a new {@code EventMessage} with the specified byte array starting from the given {@code offset} and with the given {@code length}.
     *
     * @param bytes  the content
     * @param offset the start index
     * @param length the length of the content
     */
    public Message(byte[] bytes, int offset, int length) {
        this.bytes = new byte[length];
        System.arraycopy(bytes, offset, this.bytes, 0, length);
    }

    /**
     * Constructs a new {@code EventMessage} with the specified byte array.
     *
     * @param bytes the content
     */
    public Message(byte[] bytes) {
        this(bytes, 0, bytes.length);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Message other)) {
            return false;
        }
        if (bytes.length != other.bytes.length) {
            return false;
        }
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] != other.bytes[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (byte b : bytes) {
            result = 31 * result + b;
        }
        return result;
    }

    @Override
    public String toString() {
        if (bytes.length > 64) {
            return "Message[length=" + bytes.length + "]";
        }
        return new String(bytes);
    }

    /**
     * Delivers the content of the message.
     *
     * @return the byte array
     */
    public byte[] getBytes() {
        return bytes;
    }

    /**
     * Delivers the length of the byte array.
     *
     * @return the length of the byte array
     */
    public int getLength() {
        return bytes.length;
    }
}