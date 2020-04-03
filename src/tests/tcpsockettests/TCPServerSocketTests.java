package tests.tcpsockettests;

import java.io.IOException;

import org.junit.Test;

import server.library.HttpRequestObserver;
import server.library.HttpServer;
import tcpsockets.TCPClientSocket;
import tcpsockets.TCPServerSocket;

public class TCPServerSocketTests {
	
	class TestServer implements Runnable {
        private TCPServerSocket serverSocket;

		public TestServer() {
        	serverSocket = new TCPServerSocket(80);
    		
        }

        @Override
        public void run() {
        	while(true) {
        		serverSocket.accept();
        	}
        }
    }
	
	@Test
	public void WhenStartServer_ThenCanAcceptClients() {
		//given
		
		//when
		new TestServer();
		
		//then
		TCPClientSocket client = new TCPClientSocket("localhost", 80);
	}
}
