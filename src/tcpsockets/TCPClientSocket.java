package tcpsockets;

import static java.nio.channels.SelectionKey.OP_READ;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;

public class TCPClientSocket extends TCPSocket{
	private InetSocketAddress remoteAddress;
	public TCPClientSocket(String host, int port) {
		super(host, port);
		remoteAddress = new InetSocketAddress(host, port);
	}

	@Override
	public void setupChannel(InetSocketAddress address) {
		try {
			channel.bind(null);
			channel.connect(address);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String send(String data) throws IOException { //TODO we might want to handle exceptions here instead of passing it
		Packet packet = new Packet.PacketBuilder()
			.setPacketType(PacketType.SYN)
			.setSequenceNumber(1)
			.setPeerAddress(remoteAddress.getHostName())
			.setPort(remoteAddress.getPort())
			.setData(data.getBytes())
			.build();
			
		channel.write(packet.toByteBuffer());

		
		channel.configureBlocking(false);
		Selector selector = Selector.open();
		channel.register(selector, OP_READ);
		selector.select(1000);
		
		Set<SelectionKey> keys = selector.selectedKeys();
		
		ByteBuffer response = ByteBuffer.allocate(1024);
		channel.receive(response);
		
		
		keys.clear();
		return response.toString();
		
	}


}
