package tcpsockets;

import java.io.IOException;
import java.net.InetSocketAddress;

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

	@Override
	public String send(String data) {
		// TODO Auto-generated method stub
		return null;
	}

}
