package tests.tcpsockettests;

import java.io.IOException;

import org.junit.Test;

import server.library.HttpRequestObserver;
import server.library.HttpServer;
import tcpsockets.TCPClientSocket;
import tcpsockets.TCPServerSocket;

public class TCPServerSocketTests {
	

	@Test
	public void canStartServer() {
		TCPServerSocket server = new TCPServerSocket(80);
	}
}
