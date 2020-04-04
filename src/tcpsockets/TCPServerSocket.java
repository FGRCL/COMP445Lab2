package tcpsockets;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.logging.Logger;

public class TCPServerSocket extends TCPSocket{
    static Logger log = Logger.getLogger(TCPServerSocket.class.getName());

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
                SocketAddress remote = channel.receive(dst);
                /**
                 * Normally, the router will change the packet peerAddress and port to match the address and port
                 * of the client. However, our packet is not going through the router, so it remains the address
                 * and port of the client. This is a temporary workaround to acquire the address and port of
                 * the client from the SocketAddress returned by channel.receive()
                 */
                String[] clientInfo = remote.toString().split(":");
                String clientAddress = clientInfo[0].substring(1);
                int clientPort = Integer.parseInt(clientInfo[1]);
                log.info("Received packet from " + remote);
				Packet ack = Packet.makePacket(dst);
				if(ack.getPacketType() == PacketType.SYN && ack.getSequenceNumber() == 0) {
					return new TCPServerConnectionSocket(clientAddress, clientPort);
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
