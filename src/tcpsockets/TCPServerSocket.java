package tcpsockets;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
		while(true) {
			try {
				ByteBuffer dst = ByteBuffer.allocate(Packet.MAX_PACKET_SIZE).order(ByteOrder.BIG_ENDIAN);
				dst.clear();
				channel.receive(dst);
				Packet ack = Packet.makePacket(dst);
				if(ack.getPacketType() == PacketType.SYN && ack.getSequenceNumber() == 0) {
					return new TCPServerConnectionSocket(ack.getPeerAddress(), ack.getPort());
				}
				 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void send(String data) {
		// TODO Auto-generated method stub
		
	}
}
