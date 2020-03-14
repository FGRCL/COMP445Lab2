package tcpsockets;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class TCPClientSocket extends TCPSocket{
	public TCPClientSocket(String host, int port) {
		super(host, port);
	}
	
	public void send(byte[] data) {

	}

	@Override
	public void setupChannel(InetSocketAddress address) {
		try {
			channel.connect(address);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
