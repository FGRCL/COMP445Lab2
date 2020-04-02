package tcpsockets;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

public abstract class TCPSocket {
	protected DatagramChannel channel;
	
	public TCPSocket(String host, int port) {
		try {
			channel = DatagramChannel.open();
			setupChannel(new InetSocketAddress(host, port));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public abstract void setupChannel(InetSocketAddress address);
		
	public abstract void send(String data);
		
	public abstract String receive();
}
