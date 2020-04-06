package tests.tcpsockettests;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import router.Router;
import tcpsockets.SocketClosedException;
import tcpsockets.TCPClientSocket;
import tcpsockets.TCPServerSocket;

public class TCPClientSocketTest {
	private TestServer server;
	
	class TestServer implements Runnable {
        private TCPServerSocket socket;
        private boolean running;
        public TestServer(int port) {
            socket = new TCPServerSocket(port);
            running = true;
        }

        @Override
        public void run() {
        	while(running) {
        		try {
					socket.accept();
				} catch (SocketClosedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        }
        
        public void close() {
        	running = false;
        }
    }
	
	@BeforeAll
	public void setup() {
		server = new TestServer(8080);
		Router.start(3000, 0, "0", 0);
	}
	
	@AfterAll
	public void teardown() {
		server.close();
		Router.stop();
	}
	
	@Test
	public void CanWriteToStream() {
		TCPClientSocket client = new TCPClientSocket(new InetSocketAddress("localhost", 8080), new InetSocketAddress("localhost", 3000));
		try {
			client.getOutputStream().write((byte)1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void CanRead() {
		TCPClientSocket client = new TCPClientSocket(new InetSocketAddress("localhost", 8080), new InetSocketAddress("localhost", 3000));
		try {
			client.getInputStream().read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
