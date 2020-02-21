package cliparser;

public class OptionDoesNotExistException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public OptionDoesNotExistException(String message) {
		super(message);
	}
}
