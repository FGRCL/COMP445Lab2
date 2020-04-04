package tcpsockets;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

public abstract class TCPSocket {
	protected DatagramChannel channel;
    
    /**
     * Creates a new TCP socket that uses a datagram channel
     * to send datagrams to the target address
     * @param target The address to which datagrams will be sent
     */
	public TCPSocket(InetSocketAddress targetAddress) {
		try {
			channel = DatagramChannel.open();
		} catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
		}
	}
	
	public abstract void setupChannel();
		
	public abstract void send(String data);
		
	public abstract String receive();
}
