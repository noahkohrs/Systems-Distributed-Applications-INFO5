package task3.impl;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EventPump {
    public static Queue<EventTask> queue = new ConcurrentLinkedQueue<>();

    public static void post(EventTask r) {
        queue.add(r);
    }

    public static void run() throws InterruptedException {
        while (true) {
            if (!queue.isEmpty()) {
                EventTask task = queue.poll();
                task.run();
            }
        }
    }

    static {
        new Thread(() -> {
            try {
                run();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "EventPump").start();
    }
}
