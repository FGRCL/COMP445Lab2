package tests;

import client.HelpMessages;
import client.Httpc;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;

public class ClientAcceptanceTests {
    
    @Test
    public void testGeneralHelp() {
        Httpc command = new Httpc();
        String[] args = new String[1];
        args[0] = "help";
        String response = command.execute(args);
        assertEquals(HelpMessages.BasicHelpMessage, response);
    }
    
    @Test
    public void testGetUsageHelp() {
        Httpc command = new Httpc();
        String[] args = new String[2];
        args[0] = "help";
        args[1] = "get";
        String response = command.execute(args);
        assertEquals(HelpMessages.GetUsageHelpMessage, response);
    }
    
    @Test
    public void testPostUsageHelp() {
        Httpc command = new Httpc();
        String[] args = new String[2];
        args[0] ="help";
        args[1] = "post";
        String response = command.execute(args);
        assertEquals(HelpMessages.PostUsageHelpMessage, response);
    }
    
    @Test
    public void testGetWithQueryParams() {
        Httpc command = new Httpc();
        String[] args = new String[2];
        args[0] = "get";
        args[1] = "http://httpbin.org/get?course=networking&assignment=1";
        String response = command.execute(args);
        assertTrue(!response.contains("HTTP/1.0 200 OK"));
        assertTrue(response.charAt(0) == '{');
        assertTrue(response.contains("\"assignment\""));
        assertTrue(response.contains("\"networking\""));
    }
    
    @Test
    public void testGetWithQueryParamsVerbose() {
        Httpc command = new Httpc();
        String[] args = new String[3];
        args[0] = "get";
        args[1] = "-v";
        args[2] = "http://httpbin.org/get?course=networking&assignment=1";
        String response = command.execute(args);
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("\"assignment\""));
        assertTrue(response.contains("\"networking\""));
    }
    
    @Test
    public void testPostWithInlineData() {
        Httpc command = new Httpc();
        String[] args = new String[6];
        args[0] ="post";
        args[1] = "-h";
        args[2] = "Content-Type:application/json";
        args[3] = "-d";
        args[4] = "{\"Assignment\": 1}";
        args[5] = "http://httpbin.org/post";
        String response = command.execute(args);
        
        assertTrue(response.charAt(0) == '{');
        assertTrue(response.contains("\"json\": {\n    \"Assignment\": 1"));
        assertTrue(response.contains("\"data\": \"{\\\"Assignment\\\": 1}\""));
    }
    
    @Test
    public void testRedirect() {
        Httpc command = new Httpc();
        String[] args = new String[3];
        args[0] = "get";
        args[1] = "http://google.com/";
        args[2] = "-v";
        // args[3] = "-h";
        // args[4] = "Host: www.example.org";
        
        String response = command.execute(args);
        assertTrue(response.contains("HTTP/1.0 200 OK"));
    }
    
    @Test
    public void testFileOutput() {
        Httpc command = new Httpc();
        String[] args = new String[4];
        args[0] = "get";
        args[1] = "-o";
        args[2] = "hello.txt";
        args[3] = "http://httpbin.org/get";
        
        String response = command.execute(args);
        assertTrue(response.equals("Wrote output to hello.txt"));
        new File("hello.txt").delete();
    }
}
