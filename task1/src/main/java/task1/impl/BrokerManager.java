package task1.impl;

import java.util.HashSet;
import java.util.Set;

public class BrokerManager {

    static Set<LocalBroker> brokers = new HashSet<>();

        public static synchronized void addBroker(LocalBroker broker) {
            brokers.add(broker);
        }

        public static synchronized void removeBroker(LocalBroker broker) {
            brokers.remove(broker);
        }

        public static synchronized LocalBroker getBroker(String name) {
            for (LocalBroker broker : brokers) {
                if (broker.name.equals(name)) {
                    return broker;
                }
            }
            throw new IllegalArgumentException("No broker with name " + name);
        }
}
