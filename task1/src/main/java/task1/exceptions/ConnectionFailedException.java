package task1.exceptions;

public class ConnectionFailedException extends Exception{
    public enum Issue {
        NAME_IN_USE,
        NO_BROKER_WITH_NAME,
        PORT_IN_USE_FOR_THIS_BROKER,
    }
	public ConnectionFailedException(Issue issue, String val) {
        switch (issue) {
            case NAME_IN_USE:
                System.out.println("Broker with name " + val + " already exists.");
                break;
            case NO_BROKER_WITH_NAME:
                System.out.println("No broker with name " + val);
                break;
            case PORT_IN_USE_FOR_THIS_BROKER:
                System.out.println("Port " + val + " is already in use for this broker.");
                break;
        }
    }
}
