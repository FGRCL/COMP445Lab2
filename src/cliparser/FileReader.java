package cliparser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileReader {
	public static String getFileContent(File file) {
		StringBuilder fileContent = new StringBuilder();
		try {
			FileInputStream fileReader = new FileInputStream(file);
			int character = fileReader.read();
			while(character != -1){
				fileContent.append((char)character);
				character = fileReader.read();
			}
			fileReader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileContent.toString();
	}
}
