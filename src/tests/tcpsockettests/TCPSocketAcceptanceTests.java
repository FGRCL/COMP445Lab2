package tests.tcpsockettests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Random;

import org.junit.Test;

import router.Router;
import tcpsockets.SocketClosedException;
import tcpsockets.TCPClientSocket;
import tcpsockets.TCPServerConnectionSocket;
import tcpsockets.TCPServerSocket;
import tcpsockets.TCPSocket;

public class TCPSocketAcceptanceTests {
	
	class TestServer implements Runnable {
        private TCPServerSocket socket;
        private boolean running;
        private TCPSocket connection;
        public TestServer(int port) {
            socket = new TCPServerSocket(port);
            running = true;
        }

        @Override
        public void run() {
        	while(running) {
                if(connection == null)
					try {
						connection = socket.accept();
					} catch (SocketClosedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        	}
        }
        
        public void close() {
        	running = false;
        }

        public InputStream getInputStream() throws IOException {
            return connection.getInputStream();
        }

        public OutputStream getOutputStream() throws IOException {
            return connection.getOutputStream();
        }
    }

    class Responder implements Runnable {
        BufferedReader reader;
        OutputStream output;
        String expected;
        String response;

        public Responder(InputStream input, OutputStream output, String expected, String response) {
            reader = new BufferedReader(new InputStreamReader(input));
            this.output = output;
            this.expected = expected;
            this.response = response;
        }
        @Override
        public void run() {
            try {
                String line = reader.readLine();
                if(line.equals(expected)) {
                    output.write(response.toString().getBytes("UTF-8"));
                    output.write("\r\n".toString().getBytes("UTF-8"));
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
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

        String sentMessage = "This is a test sent message.";
        String expectedResponse = "This is the response that we expect to get.";
        try {
            Thread.sleep(1000);
            Responder responder = new Responder(server.getInputStream(), server.getOutputStream(), sentMessage, expectedResponse);
            Thread thread = new Thread(responder);
            thread.start();
            OutputStream output = client.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

            output.write(sentMessage.toString().getBytes("UTF-8"));
            output.write("\r\n".toString().getBytes("UTF-8"));
            String response = reader.readLine();
            assert(response.equals(expectedResponse));
        } catch(Exception e) {
            assert(false);
        } finally {
            Router.stop();
        }
	}
	
	@Test
	public void testWithManyPackets() {
		//given
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
        StringBuilder message = new StringBuilder();
        Random random = new Random(12345);
        for(int i = 0; i<10; i++) {
        	message.append((char)random.nextInt());
        }
        //when
        try {
            Thread.sleep(1000);
            Responder responder = new Responder(server.getInputStream(), server.getOutputStream(), message.toString(), message.toString());
            Thread thread = new Thread(responder);
            thread.start();
            OutputStream output = client.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

            output.write(message.toString().getBytes("UTF-8"));
            output.write("\r\n".toString().getBytes("UTF-8"));
            
            String response = reader.readLine();
            client.close();
        //then
        assert(response.equals(message));
		} catch(Exception e) {
	        assert(false);
	    } finally {
	        Router.stop();
	    }
	}
}
