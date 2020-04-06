package tests;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;

import client.Client;
import client.Options;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;

import server.Httpfs;

public class AcceptanceTests {
    private final String serverUrl = "http://localhost:8080/";
    private final String responseOkStatusLine = "HTTP/1.0 200 OK";
    
    private TestServer server;
    private Thread serverThread;
    @BeforeEach
    public void startServer() {
        server = new TestServer();
        serverThread = new Thread(server);
        serverThread.start();
    }

    @AfterEach
    public void stopServer() {
        server.stop();
        try {
			serverThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        ;
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
    
    @AfterAll
    public static void cleanupFiles() {
    	TestUtils.deleteDirectory(new File("User_Files"));
    }
	
	@Test
	public void whenUploadTextFile_ThenShouldBeSuccessful() {
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
	public void whenUploadTextFile_ThenShouldBeListed() {
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
	public void whenUploadTextFile_ThenContentShouldSameWhenDownloaded() {
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
    
    @Test
    public void whenGetNonexistentFile_ThenResponseShouldBeNotFound() {
        String filename = "whenGetNonexistentFileShouldBeNotFound";

        Options options = new Options();
        options.method = "get";
        options.url = serverUrl + filename;
        options.verbose = true;

        String response = new Client(options).sendRequest();

        assert(response.startsWith("HTTP/1.0 404 NOT FOUND"));
    }

    @Test
    public void whenGetFileOutsideDirectory_ThenResponseShouldBeForbidden() {
        String filename = "../text.txt";

        Options options = new Options();
        options.method = "get";
        options.url = serverUrl + filename;
        options.verbose = true;

        String response = new Client(options).sendRequest();

        assert(response.startsWith("HTTP/1.0 403 FORBIDDEN"));
    }
    
    @Test
    public void whenOverwriteFile_ThenContentShouldChange() {
    	 String fileName = "whenUploadFileThenContentShouldBeDownloaded";
         String fileContents = fileName + "Content";
         String modifiedFileContents = fileName + "New Content";

         Options uploadOptions = new Options();
         uploadOptions.method = "post";
         uploadOptions.url = serverUrl + fileName;
         uploadOptions.verbose = true;
         uploadOptions.inlineData = fileContents;

         String uploadResponse = new Client(uploadOptions).sendRequest();

         Options downloadOptions = new Options();
         downloadOptions.method = "get";
         downloadOptions.url = serverUrl + fileName;
         downloadOptions.verbose = false;
         
         String downloadResponse = new Client(downloadOptions).sendRequest();
         
         uploadOptions.url += "?overwrite=true";
         uploadOptions.inlineData = modifiedFileContents;
         
         uploadResponse = new Client(uploadOptions).sendRequest();
         
         assert(uploadResponse.startsWith(responseOkStatusLine));
         
         downloadResponse = new Client(downloadOptions).sendRequest();
         
         assert(downloadResponse.equals(modifiedFileContents));
    }
    
    @Test
    public void whenWriteToExistingFile_ThenShouldGetBadRequest() {
    	String fileName = "whenUploadFileThenContentShouldBeDownloaded";
        String fileContents = fileName + "Content";
        String modifiedFileContents = fileName + "New Content";

        Options uploadOptions = new Options();
        uploadOptions.method = "post";
        uploadOptions.url = serverUrl + fileName;
        uploadOptions.verbose = true;
        uploadOptions.inlineData = fileContents;

        String uploadResponse = new Client(uploadOptions).sendRequest();

        Options downloadOptions = new Options();
        downloadOptions.method = "get";
        downloadOptions.url = serverUrl + fileName;
        downloadOptions.verbose = false;
        
        String downloadResponse = new Client(downloadOptions).sendRequest();
                
        uploadResponse = new Client(uploadOptions).sendRequest();
        
        assert(uploadResponse.startsWith("HTTP/1.0 400 BAD REQUEST"));

    }
    
}
