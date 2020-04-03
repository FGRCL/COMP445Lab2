package tests.tcpsockettests;

import java.io.IOException;

import org.junit.Test;

import server.library.HttpRequestObserver;
import server.library.HttpServer;
import tcpsockets.TCPClientSocket;
import tcpsockets.TCPServerSocket;

public class TCPSocketAcceptanceTests {
	
	class TestServer implements Runnable {
        private TCPServerSocket socket;
        private boolean running;
        public TestServer() {
            socket = new TCPServerSocket(8080);
            running = true;
        }

        @Override
        public void run() {
        	while(running) {
        		socket.accept();
        	}
        }
        
        public void close() {
        	running = false;
        }
    }
	
	@Test
	public void CanPerformHandshake() {
		TestServer server = new TestServer();
		Thread t = new Thread(server);
        t.start();
		TCPClientSocket client;
		client = new TCPClientSocket("localhost", 8080);
	}

}
