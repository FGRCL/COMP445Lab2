package tcpsockets.exceptions;

public class TimeoutExceededException extends Exception {
    public TimeoutExceededException(String message) {
        super(message);
    }
}