package client;

public class SplitUrl {
    private String domain;
    private String path;
    private int port = 80;
    public SplitUrl(String url) {
        int start = url.indexOf("://");
        start += start == -1 ? 1 : 3;
        int portStart = 0;
        
        for(int i = start; i < url.length(); i++) {
            char c = url.charAt(i);
            if(c == ':') {
                domain = url.substring(start, i);
                portStart = i + 1;
            }
            
            if(c == '/') {
                if(domain == null)
                    domain = url.substring(start, i);
                else
                    port = Integer.parseInt(url.substring(portStart, i));
                path = url.substring(i);
                return;
            }
        }
        
        domain = url.substring(start);
        path = "";
    }
    
    public String getDomain() {
        return domain;
    }
    
    public String getPath() {
        return path;
    }
    
    public int getPort() {
        return port;
    }
}
