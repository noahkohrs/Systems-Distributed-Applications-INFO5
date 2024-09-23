package task1.exceptions;

public class ConnectionFailedException extends Exception{
	public ConnectionFailedException(String name) {
        super("The name " + name + " is already in use.");
    }
}
