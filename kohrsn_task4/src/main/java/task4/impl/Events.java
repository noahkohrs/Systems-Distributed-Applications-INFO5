package task4.impl;

import task4.EventTask;

class WriteEvent extends EventTask {

    public WriteEvent(String valueRepr, LocalChannel sender, LocalChannel receiver, Runnable runnable) {
        super((valueRepr==null) ? "WriteCallback": "WriteCallback : " + valueRepr, () -> {
            if (sender.disconnected()) {
                return;
            }
            runnable.run();
        });
    }
}

class ReadEvent extends EventTask {

    public ReadEvent(LocalChannel receiver) {
        super("ReadEvent", () -> {
            if (receiver.disconnected()) {
                return;
            }
            receiver.isAwareOfChanges = false;
            receiver.readListener.received(receiver);
        });
    }
}

class ClosedEvent extends EventTask {

    public ClosedEvent(LocalChannel receiver, LocalChannel sender) {
        super("ReadEvent", () -> {
            receiver.connected = false;
            sender.connected = false;
            receiver.readListener.closed();
        });
    }
}