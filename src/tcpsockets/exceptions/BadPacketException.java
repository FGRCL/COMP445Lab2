package tcpsockets.exceptions;

public class BadPacketException extends Exception {
    public BadPacketException(String message) {
        super(message);
    }
}