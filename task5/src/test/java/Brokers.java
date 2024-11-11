import task4.impl.LocalBroker;
import task5.impl.QueueBrokerImpl;

public class Brokers {
    private static final LocalBroker localBroker = new LocalBroker("LocalBroker");
    public static final QueueBrokerImpl localQueueBroker = new QueueBrokerImpl(localBroker);

    // not remote at all in reality
    private static final LocalBroker remoteBroker = new LocalBroker("RemoteBroker");
    public static final QueueBrokerImpl remoteQueueBroker = new QueueBrokerImpl(remoteBroker);
}
