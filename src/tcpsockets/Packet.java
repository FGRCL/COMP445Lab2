package tcpsockets;

import java.nio.ByteBuffer;

public class Packet {
	private int MAX_PACKET_SIZE = 1024;
	private int HEADER_SIZE = 11;
	private PacketType packetType = PacketType.ACK;
	private long sequenceNumber;
	private String peerAddress;
	private int port;
	private byte[] data;
	private Packet(PacketType packetType, long sequenceNumber, String peerAddress, int port, byte[] data) {
		super();
		this.packetType = packetType;
		this.sequenceNumber = sequenceNumber;
		this.peerAddress = peerAddress;
		this.port = port;
		this.data = data;
	}
	
	public static class PacketBuilder{
		private PacketType packetType = PacketType.ACK;
		private long sequenceNumber = 1L;
		private String peerAddress = "0.0.0.0";
		private int port = 0;
		private byte[] data = new byte[0];
		public PacketBuilder setPacketType(PacketType packetType) {
			this.packetType = packetType;
			return this;
		}
		public PacketBuilder setSequenceNumber(long sequenceNumber) {
			this.sequenceNumber = sequenceNumber;
			return this;
		}
		public PacketBuilder setPeerAddress(String peerAddress) {
			this.peerAddress = peerAddress;
			return this;
		}
		public PacketBuilder setPort(int port) {
			this.port = port;
			return this;
		}
		public PacketBuilder setData(byte[] data) {
			this.data = data;
			return this;
		}
		public Packet build() {
			return new Packet(packetType, sequenceNumber, peerAddress, port, data);
		}
	}
	
	public ByteBuffer toByteBuffer() {
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		int value = packetType.getValue();
		byteBuffer.putInt(value);
		byteBuffer.put((byte) sequenceNumber);
		byteBuffer.put(peerAddress.getBytes());
		byteBuffer.putInt(port);
		byteBuffer.put(data);
		byte[] bytes = new byte[HEADER_SIZE + data.length];
		
		return byteBuffer;
	}
}
