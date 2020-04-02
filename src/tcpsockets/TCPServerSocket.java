package tcpsockets;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

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
	
	public String receive() {
		ByteBuffer buf = ByteBuffer.allocate(Packet.MAX_PACKET_SIZE);
		try {
			channel.receive(buf);	
		} catch (IOException e) {
			e.printStackTrace();
		}
		Packet packet = Packet.makePacket(buf);
		return packet.getPayload();
	}
	
	public TCPSocket accept() {
		try {
			Selector selector = Selector.open();
			channel.register(selector, SelectionKey.OP_READ);
			channel.configureBlocking(true);
			
			ByteBuffer dst = ByteBuffer.allocate(Packet.MAX_PACKET_SIZE);
			channel.receive(dst);
			Packet ack = Packet.makePacket(dst);
			
			 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void send(String data) {
		// TODO Auto-generated method stub
		
	}
}
