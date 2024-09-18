package task1.exceptions;

public class AlreadyInUseException extends Exception{
    public AlreadyInUseException(String name) {
        super("The name " + name + " is already in use.");
    }
}
