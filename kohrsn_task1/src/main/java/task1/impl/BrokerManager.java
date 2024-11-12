package task1.impl;

import task1.Broker;

import java.util.HashMap;
import java.util.Map;

public class BrokerManager {

    private static final Map<String, Broker> brokers = new HashMap<>();

    public static synchronized void addBroker(LocalBroker broker) {
        if (brokers.containsKey(broker.name)) {
            throw new IllegalArgumentException("A broker with name " + broker.name + " already exists");
        }
        brokers.put(broker.name, broker);
    }

    public static synchronized void removeBroker(String name) {
        brokers.remove(name);
    }

    public static synchronized Broker getBroker(String name) {
        if (brokers.containsKey(name)) {
            return brokers.get(name);
        }
        throw new IllegalArgumentException("No broker with name " + name);
    }
}
