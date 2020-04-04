package tcpsockets.exceptions;

public class BadPacketException extends Exception {
    private static final long serialVersionUID = 7907240578208164507L;

    public BadPacketException(String message) {
        super(message);
    }
}