package directory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NotDirectoryException;

public class FileDirectory {
	File root;
	
	public FileDirectory(String root){
		this.root = new File(root);
		this.root.mkdir();
	}
	
	public String listFiles(String path) throws NotDirectoryException, FileNotFoundException, FileOutsideDirectoryException {
		File directory = new File(root.getPath()+path);
		validateWithinRoot(directory);
		validateFileExists(directory, path);
		if(!directory.isDirectory()) {
			throw new NotDirectoryException(path + " is not a directory");
		}
		return String.join("\r\n", directory.list());
	}
	
	public String getFileContent(String path) throws FileOutsideDirectoryException, IOException {
		File file = new File(root+path);
		validateWithinRoot(file);
		validateFileExists(file, path);
		StringBuilder fileContent = new StringBuilder();
		
		//File Reading
		FileInputStream os = new FileInputStream(file);
		int character;
		character = os.read();
		while(character != -1) {
			fileContent.append((char)character);
			character = os.read();
		}
		os.close();
		return fileContent.toString();
	}
	
	public void createFile(String path, String content, boolean overwrite) throws FileOutsideDirectoryException, FileNotFoundException, IOException {
		File file = new File(root+path);
		validateWithinRoot(file);
		if(path.endsWith("/")) {
			throw new FileNotFoundException(path + " is not a file");
		}
		if(!file.createNewFile() && !overwrite) {
			throw new FileAlreadyExistsException(path);
		}else {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(content.getBytes());
			fos.close();
		}

	}
	
	private void validateFileExists(File file, String path) throws FileNotFoundException {
		if(!file.exists()) {
			throw new FileNotFoundException("file: "+path+"does not exist in the directory");
		}
	}
	
	private void validateWithinRoot(File file) throws FileOutsideDirectoryException {
		if(file.toString().contains("..")) {
			throw new FileOutsideDirectoryException(file+"is outside the directory");
		}
	}
}
