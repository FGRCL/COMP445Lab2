package tcpsockets;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

public abstract class TCPSocket {
	protected DatagramChannel channel;
	
	public TCPSocket(String host, int port) {
		try{
			DatagramChannel channel = DatagramChannel.open();
			this.channel = channel;
			setupChannel(new InetSocketAddress(host, port));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public abstract void setupChannel(InetSocketAddress address);
}
