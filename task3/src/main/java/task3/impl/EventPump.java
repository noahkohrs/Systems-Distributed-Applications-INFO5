package task3.impl;

import java.util.LinkedList;
import java.util.Queue;

public class EventPump {
    public static Object locker = new Object();
    public static Queue<EventTask> queue = new LinkedList<>();

    public static void post(EventTask r) {
        synchronized (locker) {
            locker.notify();
            queue.add(r);
        }
    }
    public static void post(String taskName, Runnable action) {
        post(new EventTask(taskName, action));
    }

    public static void run() throws InterruptedException {
        while (true) {
            synchronized (locker) {
                if (!queue.isEmpty()) {
                    EventTask task = queue.poll();
                    task.run();
                } else {
                    locker.wait();
                }
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
