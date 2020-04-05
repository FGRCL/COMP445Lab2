package tcpsockets;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;
import java.util.logging.Logger;

public class TCPServerConnectionSocket extends TCPSocket{
    Logger log = Logger.getLogger(TCPServerConnectionSocket.class.getName());
    
    public TCPServerConnectionSocket(InetSocketAddress clientAddress, InetSocketAddress routerAddress) {
        super(clientAddress, routerAddress);
    }
	
	@Override
	public void setupChannel() {
		try {
            channel.configureBlocking(false);
            channel.connect(routerAddress);
			if(handshakeSuccessful()) {
				log.info("Handshake successful with " + targetAddress.getHostName() + ":" + targetAddress.getPort());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean handshakeSuccessful() throws IOException {
		Packet synackPacket  = new Packet.PacketBuilder()
				.setPacketType(PacketType.SYNACK)
				.setSequenceNumber(1)
				.setPeerAddress(targetAddress.getAddress())
				.setPort(targetAddress.getPort())
				.build();
			
            Selector sel;
            log.info("Sending SYNACK to " + targetAddress.getPort() + " through router " + routerAddress.getPort());
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
}
