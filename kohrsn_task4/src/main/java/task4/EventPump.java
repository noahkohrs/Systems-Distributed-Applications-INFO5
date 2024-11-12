package task4;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Singleton.
 */
public class EventPump {
    public static Object locker = new Object();
    public static Queue<EventTask> queue = new LinkedList<>();

    public static void post(EventTask r) {
        synchronized (locker) {
            queue.add(r);
            locker.notify();
        }
    }

    public static void post(String taskName, Runnable action) {
        post(new EventTask(taskName, action));
    }

    public static void post(List<EventTask> tasks) {
        synchronized (locker) {
            queue.addAll(tasks);
            locker.notify();
        }
    }


    private static void run() throws InterruptedException {
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
