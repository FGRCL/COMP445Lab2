package server.library;
import java.io.IOException;
import java.net.SocketException;

import tcpsockets.SocketClosedException;
import tcpsockets.TCPServerSocket;
import tcpsockets.TCPSocket;

public class HttpServer {
    private int port;
    private HttpRequestObserver observer;
    private boolean open;
    TCPServerSocket server;

    public HttpServer(int port, HttpRequestObserver observer) {
        this.port = port;
        this.observer = observer;
        this.open = true;
    }

    public void start() {
        server = new TCPServerSocket(port);
        System.out.println("Listening on port " + port);
        listen();
    }

    private void listen() {
        while(open) {
        	TCPSocket client = null;
        	try {
        		client = server.accept();
        	}catch (SocketClosedException exception){
        		System.err.println(exception.getMessage());
        		return;
        	}
            new HttpServerThread(client, observer).start();
        }
    }

    public void close() {
    	try {
			server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}