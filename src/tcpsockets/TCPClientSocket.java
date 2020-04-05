package tcpsockets;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;
import java.util.logging.Logger;

import tcpsockets.exceptions.BadPacketException;
import tcpsockets.exceptions.TimeoutExceededException;

public class TCPClientSocket extends TCPSocket{
    static Logger log = Logger.getLogger(TCPClientSocket.class.getName());
    
    public TCPClientSocket(InetSocketAddress serverAddress, InetSocketAddress routerAddress) {
        super(serverAddress, routerAddress);
    }

	@Override
	public void setupChannel() {
		try {
            channel.connect(routerAddress);
            performHandshake();
            log.info("Received SYNACK from " + targetAddress.getHostName() + ":" + targetAddress.getPort());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private InetSocketAddress performHandshake() throws IOException {
        while(true) {
            try {
                return synchronize();
            } catch(TimeoutExceededException e) {
                System.out.println(e.getMessage());
            } catch(BadPacketException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    
    private InetSocketAddress synchronize() throws IOException,
        BadPacketException, TimeoutExceededException {
        // Create the syn packet to be sent to the server
		Packet synPacket = new Packet.PacketBuilder()
            .setPacketType(PacketType.SYN)
            .setSequenceNumber(0)
            .setPeerAddress(targetAddress.getAddress())
            .setPort(targetAddress.getPort())
            .build();

        int SYN_TIMEOUT = 1000;
            
        // Send the syn packet over the datagram channel
        Selector selector = Selector.open();
		channel.send(synPacket.toByteBuffer(), routerAddress);
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

            targetAddress = new InetSocketAddress(synackPacket.getPeerAddress(), synackPacket.getPort());
            sendHandshakeAckPacket();
            return targetAddress;
        } else {
            throw new BadPacketException("Expected a synack packet with sequence number 1, instead packet type was "
                + synackPacket.getPacketType() + " and sequence number was " + synackPacket.getSequenceNumber());
        }
    }

    private void sendHandshakeAckPacket() throws IOException {
        // Create the ACK packet to be sent
        Packet ackPacket = new Packet.PacketBuilder()
        .setPacketType(PacketType.ACK)
        // Since this is the first ACK, the sequence number is 1
        .setSequenceNumber(1)
        .setPeerAddress(targetAddress.getAddress())
        .setPort(targetAddress.getPort())
        .build();

        // We aren't waiting for responses from ACKs
        channel.send(ackPacket.toByteBuffer(), routerAddress);
    }
    	
    private Packet receivePacket() {
    	ByteBuffer btyeBuffer = ByteBuffer.allocate(Packet.MAX_PACKET_SIZE);
        try {
			channel.receive(btyeBuffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return Packet.makePacket(btyeBuffer);
    }
    
    private Packet makeDataPacket(byte[] data, long sequenceNumber) {
    	return new Packet.PacketBuilder()
    			.setPacketType(PacketType.DATA)
    			.setSequenceNumber(sequenceNumber)
    			.setPeerAddress(targetAddress.getAddress())
    			.setPort(targetAddress.getPort())
    			.setData(data)
    			.build();
    }
    
    private void sendAckPacket() {
    	
    }
    
    private void makeAckPacket() {
    	
    }
}
