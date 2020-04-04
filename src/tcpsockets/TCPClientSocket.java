package tcpsockets;

import tcpsockets.exceptions.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.logging.Logger;

public class TCPClientSocket extends TCPSocket{
    static Logger log = Logger.getLogger(TCPClientSocket.class.getName());
    
	private InetSocketAddress remoteAddress;
	public TCPClientSocket(String host, int port) {
		super(host, port);
		remoteAddress = new InetSocketAddress(host, port);
	}

	@Override
	public void setupChannel(InetSocketAddress address) {
		try {
            InetSocketAddress serverConnectionAddress = performHandshake(address);
            log.info("Received SYNACK from " + serverConnectionAddress.getHostName() + " port " + serverConnectionAddress.getPort());
			channel.connect(serverConnectionAddress);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public InetSocketAddress performHandshake(InetSocketAddress serverAddress) throws IOException {
        while(true) {
            try {
                return synchronize(serverAddress);
            } catch(TimeoutExceededException e) {
                System.out.println(e.getMessage());
            } catch(BadPacketException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    
    public InetSocketAddress synchronize(InetSocketAddress serverAddress) throws IOException,
        BadPacketException, TimeoutExceededException {
        // Create the syn packet to be sent to the server
		Packet synPacket = new Packet.PacketBuilder()
            .setPacketType(PacketType.SYN)
            .setSequenceNumber(0)
            .setPeerAddress(serverAddress.getAddress())
            .setPort(serverAddress.getPort())
            .build();

        int SYN_TIMEOUT = 1000;
            
        // Send the syn packet over the datagram channel
        Selector selector = Selector.open();
		channel.send(synPacket.toByteBuffer(), serverAddress);
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);
        selector.select(SYN_TIMEOUT);
        Set<SelectionKey> keys = selector.selectedKeys();
        if(keys.isEmpty()){
            // No response was received within the timeout
            throw new TimeoutExceededException("Did not receive a response to SYN within the timeout of " + SYN_TIMEOUT);
        }
        
        // A response was received: 
        ByteBuffer buf = ByteBuffer.allocate(Packet.MAX_PACKET_SIZE);
        channel.receive(buf);
        Packet synackPacket = Packet.makePacket(buf);
        if(synackPacket.getPacketType() == PacketType.SYNACK && synackPacket.getSequenceNumber() == 1) {

            InetSocketAddress newServerAddress = new InetSocketAddress(synackPacket.getPeerAddress(), synackPacket.getPort());
            sendHandshakeAckPacket(newServerAddress);
            return newServerAddress;
        } else {
            throw new BadPacketException("Expected a synack packet with sequence number 1, instead packet type was "
                + synackPacket.getPacketType() + " and sequence number was " + synackPacket.getSequenceNumber());
        }
    }

    public void sendHandshakeAckPacket(InetSocketAddress serverAddress) throws IOException {
        // Create the ACK packet to be sent
        Packet ackPacket = new Packet.PacketBuilder()
        .setPacketType(PacketType.ACK)
        // Since this is the first ACK, the sequence number is 1
        .setSequenceNumber(1)
        .setPeerAddress(serverAddress.getAddress())
        .setPort(serverAddress.getPort())
        .build();

        // We aren't waiting for responses from ACKs
        channel.send(ackPacket.toByteBuffer(), serverAddress);
    }

	@Override
	public void send(String data){ //TODO we might want to handle exceptions here instead of passing it
		Packet packet = new Packet.PacketBuilder()
			.setPacketType(PacketType.SYN)
			.setSequenceNumber(1)
			.setPeerAddress(remoteAddress.getAddress())
			.setPort(remoteAddress.getPort())
			.setData(data.getBytes())
			.build();
			
		try {
			channel.write(packet.toByteBuffer());
			channel.configureBlocking(false);
			Selector selector = Selector.open();
			channel.register(selector, SelectionKey.OP_READ);
			selector.select(1000);
			
			Set<SelectionKey> keys = selector.selectedKeys();
			
			ByteBuffer response = ByteBuffer.allocate(1024);
			channel.receive(response);
			keys.clear();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String receive() {
		// TODO Auto-generated method stub
		return null;
	}
}
