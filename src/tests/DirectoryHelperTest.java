package tests;

import static org.junit.Assert.fail;

import java.io.File;
import java.nio.file.FileAlreadyExistsException;

import org.junit.jupiter.api.*;

import directory.FileDirectory;
import directory.FileOutsideDirectoryException;

public class DirectoryHelperTest {
	final private String TESTING_DIRECTORY = "TestDirectory";
	
	@AfterEach
	public void removeDiretory() {
		deleteDirectory(new File(TESTING_DIRECTORY));
	}
	
	@Test
	public void whenCreatingNewFile_ThenFileExists() {
		FileDirectory dir = new FileDirectory(TESTING_DIRECTORY);
		String filePath = "testFile.txt";
		try {
			dir.createFile(filePath, "abc", true);
		} catch (FileAlreadyExistsException | FileOutsideDirectoryException e) {
			fail("File shouldn't have a problem");
		}
	}
	
	private boolean deleteDirectory(File directoryToBeDeleted) { //I stole that from the internet
	    File[] allContents = directoryToBeDeleted.listFiles();
	    if (allContents != null) {
	        for (File file : allContents) {
	            deleteDirectory(file);
	        }
	    }
	    return directoryToBeDeleted.delete();
	}
}
