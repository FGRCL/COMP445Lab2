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

import client.Client;
import client.Options;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import server.Httpfs;

public class AcceptanceTests {
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    private final String serverUrl = "http://localhost:8080/";
    private final String responseOkStatusLine = "HTTP/1.0 200 OK";
    
    private TestServer server;
    @BeforeEach
    public void startServer() {
        server = new TestServer();
        Thread t = new Thread(server);
        t.start();
    }

    @AfterEach
    public void stopServer() {
        server.stop();
    }

	@BeforeEach
	public void setUpStreams() {
	    // System.setOut(new PrintStream(outContent));
	    // System.setErr(new PrintStream(errContent));
    }

    class TestServer implements Runnable {
        @Override
        public void run() {
            Httpfs.main(new String[]{"-p", "8080", "-d", "testDirectory"});
        }

        public void stop() {
            Httpfs.stop();
        }
    }
	
	@Test
	public void whenUploadTextFile_ThenShouldBeSuccessful() throws IOException {
        String fileName = "whenUploadFileShouldBeSuccessful";
        String fileContent = fileName + "Content";

        //Send the request
        Options options = new Options();
        options.method = "post";
        options.url = serverUrl + fileName;
        options.verbose = true;
        options.inlineData = fileContent;
        String response = new Client(options).sendRequest();

		//then
        assertTrue(response.startsWith(responseOkStatusLine));
	}
	
	@Test
	public void whenUploadTextFile_ThenShouldBeListed() throws IOException {
        String fileName = "whenUploadFileShouldBeListed";
        String fileContent = fileName + "Content";
        
        Options uploadOptions = new Options();
        uploadOptions.method = "post";
        uploadOptions.url = serverUrl + fileName;
        uploadOptions.verbose = true;
        uploadOptions.inlineData = fileContent;
        String uploadResponse = new Client(uploadOptions).sendRequest();
        
        assertTrue(uploadResponse.startsWith(responseOkStatusLine));

        Options listingOptions = new Options();
        listingOptions.method = "get";
        listingOptions.url = serverUrl;
        listingOptions.verbose = true;
        String listingResponse = new Client(listingOptions).sendRequest();

        assertTrue(listingResponse.startsWith(responseOkStatusLine));
        assertTrue(listingResponse.contains(fileName));
	}
	
	@Test
	public void whenUploadTextFile_ThenContentShouldSameWhenDownloaded() throws IOException {
        String fileName = "whenUploadFileThenContentShouldBeDownloaded";
        String fileContents = fileName + "Content";

        Options uploadOptions = new Options();
        uploadOptions.method = "post";
        uploadOptions.url = serverUrl + fileName;
        uploadOptions.verbose = true;
        uploadOptions.inlineData = fileContents;

        String uploadResponse = new Client(uploadOptions).sendRequest();
        assert(uploadResponse.startsWith(responseOkStatusLine));

        Options downloadOptions = new Options();
        downloadOptions.method = "get";
        downloadOptions.url = serverUrl + fileName;
        downloadOptions.verbose = false;
        
        String downloadResponse = new Client(downloadOptions).sendRequest();

        assert(downloadResponse.equals(fileContents));
	}
	
	@AfterEach
	public void restoreStreams() {
		// outContent.reset();
	    // System.setOut(originalOut);
	    // System.setErr(originalErr);
	}
}
