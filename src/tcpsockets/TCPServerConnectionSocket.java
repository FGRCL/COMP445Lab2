package tcpsockets;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;

public class TCPServerConnectionSocket extends TCPSocket{
	public TCPServerConnectionSocket(String host, int port) {
		super(host, port);
	}
	
	@Override
	public void setupChannel(InetSocketAddress clientAddress) {
		try {
			if(handshakeSuccessful(clientAddress)) {
					channel.connect(clientAddress);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean handshakeSuccessful(InetSocketAddress address) throws IOException {
		Packet synackPacket  = new Packet.PacketBuilder()
				.setPacketType(PacketType.SYNACK)
				.setSequenceNumber(1)
				.setPeerAddress(address.getHostName())
				.setPort(address.getPort())
				.build();
			
			Selector sel;
			channel.write(synackPacket.toByteBuffer());
			sel = Selector.open();
			channel.register(sel, SelectionKey.OP_READ);
			sel.select(1000);
			Set<SelectionKey> keys = sel.selectedKeys();
			if(keys.isEmpty()){
				return false;
			}
			
			ByteBuffer buf = ByteBuffer.allocate(Packet.MAX_PACKET_SIZE);
			channel.receive(buf);
			Packet ackPacket = Packet.makePacket(buf);
			if(ackPacket.getPacketType() == PacketType.ACK && ackPacket.getSequenceNumber() == 1) {
				return true;
			}

		return false;
	}

	@Override
	public void send(String data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String receive() {
		// TODO Auto-generated method stub
		return null;
	}

}
