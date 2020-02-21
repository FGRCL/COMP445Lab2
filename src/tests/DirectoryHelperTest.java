package tests;

import java.io.File;

import org.junit.jupiter.api.*;

import directory.FileDirectory;

public class DirectoryHelperTest {
	final private String TESTING_DIRECTORY = "TestDirectory";
	FileDirectory dir;
	@BeforeEach
	public void createHelper(){
		dir = new FileDirectory(TESTING_DIRECTORY);
	}
	
	@AfterEach
	public void removeDiretory() {
		
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
