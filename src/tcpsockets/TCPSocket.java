package tcpsockets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

public abstract class TCPSocket implements Runnable {
    protected DatagramChannel channel;

    protected InetSocketAddress targetAddress;
    protected InetSocketAddress routerAddress;
    
    private PipedInputStream input;
    private PipedOutputStream output;
    
    /**
     * Creates a new TCP socket that uses a datagram channel
     * to send datagrams to the target address
     * @param target The address to which datagrams will be sent
     */
	public TCPSocket(InetSocketAddress targetAddress, InetSocketAddress routerAddress) {
		try {
            channel = DatagramChannel.open();
            this.targetAddress = targetAddress;
            this.routerAddress = routerAddress;
            input = new PipedInputStream();
            output = new PipedOutputStream();
            new Thread(this).start();
		} catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    @Override
    public void run() {
        setupChannel();
    }

    public abstract void setupChannel();

    public OutputStream getOutputStream() throws IOException {
        return new PipedOutputStream(input);
    }

    public InputStream getInputStream() throws IOException {
        return new PipedInputStream(output);
    }

    private void selectiveRepeat() throws IOException {
    	long currentSequenceNumber = 2;
    	long sendBase = currentSequenceNumber;
    	long receiveBase = 2;
    	int sendWindow = 3;
        int timeout = 1000;
        /*
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
        */
    }
}
