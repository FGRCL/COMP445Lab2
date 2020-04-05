package tests.tcpsockettests;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.junit.Test;

import router.Router;
import tcpsockets.TCPClientSocket;
import tcpsockets.TCPServerSocket;

public class TCPSocketAcceptanceTests {
	
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
        		socket.accept();
        	}
        }
        
        public void close() {
        	running = false;
        }
    }

	@Test
	public void CanPerformHandshake() {
        String localhost = "localhost";
        int serverPort = 8080;
        int routerPort = 3000;

        InetSocketAddress serverAddress = new InetSocketAddress(localhost, serverPort);
        InetSocketAddress routerAddress = new InetSocketAddress(localhost, routerPort);

		Router.start(3000, 0.0f, "5ms", 1);
		TestServer server = new TestServer(serverPort);
		Thread t = new Thread(server);
        t.start();
        TCPClientSocket client = new TCPClientSocket(serverAddress, routerAddress);
        try {
            client.getInputStream().read();
        } catch(IOException e) {
            assert(false);
        }
	}

}
