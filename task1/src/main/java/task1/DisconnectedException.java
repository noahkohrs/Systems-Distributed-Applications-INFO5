package task1;

public class DisconnectedException extends Exception {

    public final DisconnectionKind kind;

    public DisconnectedException(DisconnectionKind kind) {
        super("Channel is disconnected");
        this.kind = kind;
    }

    public enum DisconnectionKind {
        NATURAL,
        ERROR
    }
}
