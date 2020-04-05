package tests.tcpsockettests;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

import org.junit.Test;

import tcpsockets.TCPClientSocket;
import tcpsockets.TCPServerSocket;
import tcpsockets.TCPSocket;

public class TCPServerSocketTest {
	private class TestClient extends Thread{
		private TCPClientSocket strippedSocks;
		private String message; 
		public TestClient(InetSocketAddress target, InetSocketAddress router, String message) {
			strippedSocks = new TCPClientSocket(target, router);
			this.message = message;
		}
		
		@Override
		public void run() {
			try {
				strippedSocks.getOutputStream().write(message.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Test
	public void CanReadFromStream() {
		String expected = "Hello World!"+(char)-1;
		TestClient client = new TestClient(new InetSocketAddress("localhost", 8080), new InetSocketAddress("localhost", 3000), expected);
		
		TCPServerSocket server = new TCPServerSocket(8080);
		client.start();
		TCPSocket clientConnection = server.accept();
		
		StringBuilder receivedString = new StringBuilder();
		int character;
		try {
			InputStream is = clientConnection.getInputStream();
			character = is.read();
			while(character != -1) {
				receivedString.append((char) character);
				character = is.read();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String string = receivedString.toString();
		assert(string.equals(expected));
	}
	
}
