package tcpsockets;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.util.ArrayList;

public class TCPClientSocket extends TCPSocket{
	private Selector selector;
	public TCPClientSocket(String host, int port) {
		super(host, port);
		try {
			selector = Selector.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	@Override
	public void setupChannel(InetSocketAddress address) {
		try {
			channel.connect(address);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void send(byte[] data) {
		ArrayList<Packet> packets = new ArrayList<Packet>();
		int nbPackets = (int)data.length/Packet.MAX_PAYLOAD_SIZE;
		if (data.length%Packet.MAX_PAYLOAD_SIZE != 0) {
			nbPackets++;
		}
		for(int i=0; i<data.length; i += Packet.MAX_PAYLOAD_SIZE) {
			
		}
		
		for(Packet packet: packets) {
			channel.write(packet.toByteBuffer());
			channel.setOption(name, value)
			channel.receive(dst)
		}
	}

	@Override
	public String receive() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
