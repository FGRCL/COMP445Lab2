package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NotDirectoryException;

import org.junit.jupiter.api.*;

import directory.FileDirectory;
import directory.FileOutsideDirectoryException;

public class DirectoryHelperTest {
	final private String TESTING_DIRECTORY = "TestDirectory";
	
	@AfterEach
	public void removeDiretory() {
		TestUtils.deleteDirectory(new File(TESTING_DIRECTORY));
	}
	
	@Test
	public void whenCreatingNewFile_ThenFileExists() {
		//given
		FileDirectory dir = new FileDirectory(TESTING_DIRECTORY);
		String filePath = "/testFile.txt";
		
		//when
		try {
			dir.createFile(filePath, "abc", true);
		} catch (FileOutsideDirectoryException | IOException e) {
			fail("File shouldn't have a problem");
		}
		
		//then
		assertTrue(new File(TESTING_DIRECTORY+filePath).getName().equals("testFile.txt"));
	}
	
	@Test
	public void whenCreatingNewFile_ThenFileShouldBeListed() {
		//given
		FileDirectory dir = new FileDirectory(TESTING_DIRECTORY);
		String filePath = "/testFile.txt";
		try {
			dir.createFile(filePath, "abc", true);
		} catch (FileOutsideDirectoryException | IOException e) {
			fail("File shouldn't have a problem");
		}
		
		//when
		String files = "";
		try {
			files = dir.listFiles("/");
		} catch (NotDirectoryException | FileNotFoundException | FileOutsideDirectoryException e) {
			fail("listing files failed");
		}
		
		//then
		assertEquals("testFile.txt", files);
	}
	
	@Test
	public void whenCreatingNewFile_ThenFileShouldHaveContent() {
		//given
		FileDirectory dir = new FileDirectory(TESTING_DIRECTORY);
		String filePath = "/testFile.txt";
		try {
			dir.createFile(filePath, "abc", true);
		} catch (FileOutsideDirectoryException | IOException e) {
			fail("File shouldn't have a problem");
		}
		
		//when
		String files = "";
		try {
			files = dir.getFileContent(filePath);
		} catch (FileOutsideDirectoryException | IOException e) {
			fail("listing files failed");
		}
		
		//then
		assertEquals(files,"abc");
	}
}
