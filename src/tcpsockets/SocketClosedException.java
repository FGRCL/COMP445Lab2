package tcpsockets;

import java.net.SocketException;

public class SocketClosedException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SocketClosedException(String message) {
		super(message);
	}
}
