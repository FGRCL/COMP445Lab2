package server;

import java.io.IOException;

import server.library.HttpServer;

public class Httpfs {
    private final static String FILE_DIRECTORY = "User_Files";
    private static HttpServer server;
	public static void main(String[] args) {
        HttpfsOptions options = new HttpfsOptions(args);
        
        FileServer fs = new FileServer(FILE_DIRECTORY);
        server = new HttpServer(options.port, fs);
        server.start();

    }
    
    public static void stop() {
        server.close();
    }

}
