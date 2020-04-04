package tcpsockets.streams;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;

import tcpsockets.Packet;

public class TCPOutputStream extends OutputStream{
	private LinkedList<Byte> bytes;
	private boolean flushed;
	
	public TCPOutputStream() {
		bytes = new LinkedList<Byte>();
		flushed = false;
	}
	
	@Override
	public void write(int b) throws IOException {
		bytes.add((byte)(b & 0xFF));
	}
	
	public boolean ready() {
		return bytes.size() >= Packet.MAX_PAYLOAD_SIZE || flushed;
	}
	
	public byte[] getData() {
		byte[] data = null;
		if(ready()) {
			if(flushed) {
				flushed = false;
			}
			data = new byte[getDataSize()];
			for(int i=0; i<data.length; i++) {
				data[i] = bytes.remove();
			}
		}
		return data;
	}
	
	private int getDataSize() {
		int size = bytes.size();
		if (size > Packet.MAX_PAYLOAD_SIZE) {
			size = Packet.MAX_PAYLOAD_SIZE;
		}
		return size;
	}
	
	public void flush() {
		flushed = true;
	}

}
