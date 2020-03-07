package tests;

import java.io.File;

public class TestUtils {
	public static boolean deleteDirectory(File directoryToBeDeleted) { //I stole that from the internet
	    File[] allContents = directoryToBeDeleted.listFiles();
	    if (allContents != null) {
	        for (File file : allContents) {
	            deleteDirectory(file);
	        }
	    }
	    return directoryToBeDeleted.delete();
	}
}
