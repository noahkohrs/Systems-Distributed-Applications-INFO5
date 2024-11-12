package task1.exceptions;

public class ConnectionFailedException extends Exception {
    public enum Issue {
        NAME_IN_USE,
        NO_BROKER_WITH_NAME,
        PORT_IN_USE_FOR_THIS_BROKER,
        ERROR
    }
    public Issue issue;

	public ConnectionFailedException(Issue issue, String val) {
        super(switch (issue) {
            case NAME_IN_USE -> "Name " + val + " is already in use";
            case NO_BROKER_WITH_NAME -> "No broker with name " + val;
            case PORT_IN_USE_FOR_THIS_BROKER -> "Port " + val + " is already in use for this broker";
            case ERROR -> "An error occurred";
        });
        this.issue = issue;
    }
}
