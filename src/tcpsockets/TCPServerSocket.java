package tcpsockets;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import server.library.HttpRequest;

public class TCPServerSocket extends TCPSocket{
	public TCPServerSocket(int port) {
		super("localhost", port);
	}

	@Override
	public void setupChannel(InetSocketAddress address) {
		try {
			channel.bind(address);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
