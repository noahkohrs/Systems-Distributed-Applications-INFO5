package task4.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class BrokerManager {

    static Map<String, LocalBroker> brokers = new ConcurrentHashMap<>();

    public static void addBroker(LocalBroker broker) {
        if (brokers.containsKey(broker.name)) {
            throw new IllegalArgumentException("Broker with name " + broker.name + " already exists.");
        }
        brokers.put(broker.name, broker);
    }

    public static void removeBroker(String name) {
        brokers.remove(name);
    }

    public static LocalBroker getBroker(String name) {
        return brokers.get(name);
    }
}
