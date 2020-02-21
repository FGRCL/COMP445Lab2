package server;

import java.io.IOException;

import server.library.HttpServer;

public class Httpfs {

	public static void main(String[] args) {
        HttpfsOptions options = new HttpfsOptions(args);
        
        try {
            FileServer fs = new FileServer();
            new HttpServer(options.port, fs).start();
            new PrintServer().start();
        } catch(IOException e) {
            System.err.println("Could not open the server: " + e);
            System.exit(1);
        }
	}

}
