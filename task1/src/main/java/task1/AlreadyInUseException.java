package task1;

public class AlreadyInUseException extends Exception{
    public AlreadyInUseException(String name) {
        super("The name " + name + " is already in use.");
    }
}
