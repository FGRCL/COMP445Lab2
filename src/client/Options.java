package client;

import java.util.LinkedList;
import java.util.List;

public class Options {
    public String method;
    public boolean verbose = false;
    private final List<String> headers;
    public String inlineData;
    public String fileName;
    public String url;
    public String outFile;
    
    public Options() {
        headers = new LinkedList<>();
    }
    
    public void addHeader(String header) {
        headers.add(header);
    }
    
    public Iterable<String> getHeaders() {
        return headers;
    }
    
    public void setHostHeader(String location) {
        String hostHeader = null;
        for(String header : headers) {
            if(header.startsWith("Host:"));
                hostHeader = header;
        }
        if(hostHeader != null)
            headers.remove(hostHeader);
        
        headers.add("Host: " + location);
    }
}
