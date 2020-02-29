package directory;

public class FileOutsideDirectoryException extends Exception{
	private static final long serialVersionUID = 1L;

	FileOutsideDirectoryException(String message){
		super(message);
	}
}
