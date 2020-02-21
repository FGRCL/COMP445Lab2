package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

import httpc.client.Client;
import httpc.client.Options;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import server.Httpfs;

public class AcceptanceTests {
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;
	private final PrintStream originalErr = System.err;
	@BeforeEach
	public void setUpStreams() {
	    System.setOut(new PrintStream(outContent));
	    System.setErr(new PrintStream(errContent));
    }

    class TestServer implements Runnable {
        @Override
        public void run() {
            Httpfs.main(new String[]{});
        }

    }
    
    @Test
    public void testPrintServer() {
        Thread t = new Thread(new TestServer());
        t.start();

        Options options = new Options();
        options.method = "get";
        options.url = "http://127.0.0.1/";
        options.verbose = true;
        String response = new Client(options).sendRequest();

        assertTrue(response.startsWith("HTTP"));
    }
	
	@Test
	public void whenUploadTextFile_ThenShouldBeSuccessful() throws IOException {
		//given
		String fileContent = "Hello word";
		File textFile = new File("text.txt");
		FileOutputStream fos = new FileOutputStream(textFile);
		fos.write(fileContent.getBytes());
		
		//when
		Httpfs.main(new String[]{"post", "/"+textFile.getName()});
		
		//then
		assertTrue(true);//TODO this needs to be stronger
	}
	
	@Test
	public void whenUploadTextFile_ThenShouldBeListed() throws IOException {
		//given
		String fileContent = "Hello word";
		File textFile = new File("text.txt");
		FileOutputStream fos = new FileOutputStream(textFile);
        fos.write(fileContent.getBytes());
        		
		//when
		Httpfs.main(new String[]{"post", "/"+textFile.getName()});
		Httpfs.main(new String[]{"get", "/returns"});
		
		//then
		String consoleOut = outContent.toString();
		assertTrue(consoleOut.contains("text.txt"));//TODO replace this by an assertEquals?
	}
	
	@Test
	public void whenUploadTextFile_ThenContentShouldSameWhenDownloaded() throws IOException {
		//given
		String fileContent = "Hello word";
		File textFile = new File("text.txt");
		FileOutputStream fos = new FileOutputStream(textFile);
		fos.write(fileContent.getBytes());
		
		//when
		Httpfs.main(new String[]{"post", "/"+textFile.getName()});
		Httpfs.main(new String[]{"get", "/"+textFile.getName()});
		
		//then
		String consoleOut = outContent.toString();
		assertEquals(fileContent, consoleOut);//TODO replace this by an assertEquals?
	}
	
	@AfterEach
	public void restoreStreams() {
		outContent.reset();
	    System.setOut(originalOut);
	    System.setErr(originalErr);
	}
}
