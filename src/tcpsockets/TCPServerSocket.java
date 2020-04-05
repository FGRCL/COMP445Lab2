package tcpsockets;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.util.logging.Logger;

public class TCPServerSocket {
    static Logger log = Logger.getLogger(TCPServerSocket.class.getName());
    private DatagramChannel channel;
    InetSocketAddress myAddress;

	public TCPServerSocket(int port) {
        try {
            channel = DatagramChannel.open();
            myAddress = new InetSocketAddress("localhost", port);
            channel.bind(myAddress);
		} catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
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
                
                Packet ack = Packet.makePacket(dst);

                InetSocketAddress routerAddress = getRouterAddress(remote);
                InetSocketAddress clientAddress = getClientAddress(ack);

				if(ack.getPacketType() == PacketType.SYN && ack.getSequenceNumber() == 0) {
                    log.info("Received SYN from " + clientAddress.getHostName() + ":" + clientAddress.getPort());
                    TCPSocket connection = new TCPServerConnectionSocket(clientAddress, routerAddress);
                    return connection;
				}
				 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
    
    public InetSocketAddress getRouterAddress(SocketAddress remote) {
        String[] info = remote.toString().split(":");
        String address = info[0].substring(1);
        int port = Integer.parseInt(info[1]);
        return new InetSocketAddress(address, port);
    }

    public InetSocketAddress getClientAddress(Packet ack) {
        String host = ack.getPeerAddress().getHostAddress();
        int port = ack.getPort();
        return new InetSocketAddress(host, port);
    }
    
    public void close() throws IOException {
    	channel.close();
    }
}
