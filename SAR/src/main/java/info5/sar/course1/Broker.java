package info5.sar.course1;

abstract class Broker {
    Broker(String name) {
        throw new UnsupportedOperationException("Should be overridden");
    }

    abstract Channel accept(int port);
    abstract Channel connect(String host, int port);
}

abstract class Channel {
    abstract int read(byte[] bytes, int offset, int length);
    abstract int write(byte[] bytes, int offset, int length);
    abstract void disconnect();
    abstract boolean disconnected();
}

