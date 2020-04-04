package tcpsockets.exceptions;

public class TimeoutExceededException extends Exception {
    private static final long serialVersionUID = -3948184695183788466L;

    public TimeoutExceededException(String message) {
        super(message);
    }
}