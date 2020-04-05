package tcpsockets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;
import tcpsockets.exceptions.BadPacketException;
import tcpsockets.exceptions.TimeoutExceededException;
import tcpsockets.streams.TCPInputStream;
import tcpsockets.streams.TCPOutputStream;

public class TCPClientSocket extends TCPSocket{
    static Logger log = Logger.getLogger(TCPClientSocket.class.getName());
    
    private InetSocketAddress serverAddress;
    private InetSocketAddress routerAddress;
    
    private TCPOutputStream outputStream;
    private TCPInputStream inputStream;
    
	public TCPClientSocket(InetSocketAddress serverAddress) {
        super(serverAddress);
        this.serverAddress = serverAddress;
        this.routerAddress = serverAddress;
    }
    
    public TCPClientSocket(InetSocketAddress serverAddress, InetSocketAddress routerAddress) {
        super(routerAddress);
        this.serverAddress = serverAddress;
        this.routerAddress = routerAddress;
    }
    
    public OutputStream getOutputStream() {
		return outputStream;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	@Override
	public void setupChannel() {
		try {
            InetSocketAddress serverConnectionAddress = performHandshake();
            log.info("Received SYNACK from " + serverConnectionAddress.getHostName() + " port " + serverConnectionAddress.getPort());
			channel.connect(serverConnectionAddress);
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
            .setPeerAddress(serverAddress.getAddress())
            .setPort(serverAddress.getPort())
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

            serverAddress = new InetSocketAddress(synackPacket.getPeerAddress(), synackPacket.getPort());
            sendHandshakeAckPacket();
            return serverAddress;
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
        .setPeerAddress(serverAddress.getAddress())
        .setPort(serverAddress.getPort())
        .build();

        // We aren't waiting for responses from ACKs
        channel.send(ackPacket.toByteBuffer(), routerAddress);
    }
    
    private void slectiveRepeat() throws IOException {
    	long currentSequenceNumber = 2;
    	long sendBase = currentSequenceNumber;
    	long receiveBase = 2;
    	int sendWindow = 3;
    	int timeout = 1000;
    	HashMap<Long, PacketStatusPair> outboundPackets = new HashMap<Long, PacketStatusPair>();
    	Selector selector = Selector.open();
    	channel.configureBlocking(false);
    	channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
    	while(true) {
    		//make new packets from the outputStream
    		while(outputStream.ready()) {
    			Packet packet = makeDataPacket(outputStream.getData(), currentSequenceNumber);//handle better sequence number?
    			outboundPackets.put(currentSequenceNumber, new PacketStatusPair(packet));
    			currentSequenceNumber++;
    		}
    		
    		//timeout
    		selector.select(1000);
    		Set<SelectionKey> keys =selector.selectedKeys();
    		
    		boolean receivedAck = false;
    		if(keys.contains(SelectionKey.OP_READ)) {
    			Packet receivedPacket = receivePacket();
    			if(receivedPacket.getPacketType() == PacketType.ACK) {
    				PacketStatusPair ackedPacket = outboundPackets.get(receivedPacket.getSequenceNumber());
    				ackedPacket.status = PacketStatusPair.Status.ACK;
    				receivedAck = true;
    				//increment base
    				long newSendBase = sendBase;
    				while(outboundPackets.get(newSendBase).status == PacketStatusPair.Status.ACK) {
    					newSendBase++;
    				}
    				sendBase = newSendBase;
    			}else if(receivedPacket.getPacketType() == PacketType.DATA) {
    				//receive data
    			}
    		}
    		
    		//resend nack packets
    		if(!receivedAck) {
    			for(long i=sendBase; i<sendBase+sendWindow; i++) {
    				PacketStatusPair packetPair = outboundPackets.get(i);
    				if(packetPair.status == PacketStatusPair.Status.NACK) {
    					channel.write(packetPair.packet.toByteBuffer());
    				}
    			}
    		}
    		
    		//send new packets
    		if(keys.contains(SelectionKey.OP_WRITE)) {
    			//send some packets if some aren't sent
    			for(long i=sendBase; i<sendBase+sendWindow; i++) {
    				PacketStatusPair packetPair = outboundPackets.get(i);
    				if(packetPair.status == PacketStatusPair.Status.NOT_SENT) {
    					channel.write(packetPair.packet.toByteBuffer());
    					packetPair.status = PacketStatusPair.Status.NACK;
    				}
    			}
    		}
    	}
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
    			.setPeerAddress(serverAddress.getAddress())
    			.setPort(serverAddress.getPort())
    			.setData(data)
    			.build();
    }
    
    private void sendAckPacket() {
    	
    }
    
    private void makeAckPacket() {
    	
    }
    
	@Override
	public void send(String data){ //TODO we might want to handle exceptions here instead of passing it
		Packet packet = new Packet.PacketBuilder()
			.setPacketType(PacketType.SYN)
			.setSequenceNumber(1)
			.setPeerAddress(serverAddress.getAddress())
			.setPort(serverAddress.getPort())
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
