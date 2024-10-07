package task3.impl;

import java.util.LinkedList;
import java.util.Queue;

public class EventPump {
    public static Queue<Runnable> queue = new LinkedList<>();

    public static void post(Runnable r) {
        queue.add(r);
    }

    public static void run() throws InterruptedException {
        while (true) {
            if (!queue.isEmpty()) {
                Runnable task = queue.poll();
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
