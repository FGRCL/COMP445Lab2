package tcpsockets.streams;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import tcpsockets.Packet;

public class TCPInputStream extends InputStream{
	private LinkedList<Byte> bytes;
	
	public TCPInputStream() {
		bytes = new LinkedList<Byte>();
	}
	
	public void addPacket(Packet packet) {
		addByteArray(packet.getData());
	}
	
	public void addByteArray(byte[] data) {
		for(byte b : data) {
			bytes.add(b);
		}
	}
	
	@Override 
	public int available() {
		return bytes.size();
	}
	
	@Override
	public int read() throws IOException {
		return bytes.remove();
	}

}
