package task4.impl;

import task4.Channel;
import task4.EventTask;

class WriteEvent extends EventTask {

    public WriteEvent(LocalChannel sender, LocalChannel receiver, Runnable runnable) {
        this(null, sender, receiver, runnable);
    }

    public WriteEvent(String specialValue, LocalChannel sender, LocalChannel receiver, Runnable runnable) {
        super((specialValue==null) ? "WriteCallback": "WriteCallback : " + specialValue, () -> {
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