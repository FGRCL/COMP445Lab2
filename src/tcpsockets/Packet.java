package tcpsockets;

import java.nio.ByteBuffer;

public class Packet {
	public static int MAX_PACKET_SIZE = 1024;
	public static int MAX_PAYLOAD_SIZE = 1013;
	public static int HEADER_SIZE = MAX_PACKET_SIZE-MAX_PAYLOAD_SIZE;
	private PacketType packetType;
	private long sequenceNumber;
	private String peerAddress;
	private int port;
	private byte[] data;

	public PacketType getPacketType() {
		return packetType;
	}

	public long getSequenceNumber() {
		return sequenceNumber;
	}

	public String getPeerAddress() {
		return peerAddress;
	}

	public int getPort() {
		return port;
	}

	public byte[] getData() {
		return data;
	}

	public String getPayload() {
		StringBuffer payload = new StringBuffer();
		for(byte c: data) {
			payload.append((char) c);
		}
		return payload.toString();
	}
	
	private Packet(PacketType packetType, long sequenceNumber, String peerAddress, int port, byte[] data) {
		super();
		this.packetType = packetType;
		this.sequenceNumber = sequenceNumber;
		this.peerAddress = peerAddress;
		this.port = port;
		this.data = data;
	}
	
	public static Packet makePacket(ByteBuffer byteBuffer) {//TODO change this logic once I merge it
		PacketBuilder builder = new PacketBuilder()
				.setPacketType(PacketType.values()[byteBuffer.get(0)])
				.setSequenceNumber(byteBuffer.getInt(1))
				.setPeerAddress(byteBuffer.get(5)+"."+byteBuffer.get(6)+"."+byteBuffer.get(7)+"."+byteBuffer.get(8))
				.setPort(byteBuffer.getShort(9));
		byte[] payload = new byte[byteBuffer.remaining()];
		byteBuffer.get(payload);
		builder.setData(payload);
		return builder.build();
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
		
		return byteBuffer;
	}
}
