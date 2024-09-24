package task1.impl;

import java.util.HashMap;
import java.util.Map;

public class BrokerManager {

    static Map<String, LocalBroker> brokers = new HashMap<>();

        public static synchronized void addBroker(LocalBroker broker) {
            brokers.put(broker.name, broker);
        }

        public static synchronized void removeBroker(LocalBroker broker) {
            brokers.remove(broker.name);
        }

        public static synchronized LocalBroker getBroker(String name) {
            if (brokers.containsKey(name)) {
                return brokers.get(name);
            }
            throw new IllegalArgumentException("No broker with name " + name);
        }
}
