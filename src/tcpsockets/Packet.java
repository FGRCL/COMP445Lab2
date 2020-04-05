package tcpsockets;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Packet {
	public static int MAX_PACKET_SIZE = 1024;
	public static int MAX_PAYLOAD_SIZE = 1013;
	public static int HEADER_SIZE = MAX_PACKET_SIZE-MAX_PAYLOAD_SIZE;
	private PacketType packetType;
	private long sequenceNumber;
	private InetAddress peerAddress;
	private int port;
	private byte[] data;

	public PacketType getPacketType() {
		return packetType;
	}

	public long getSequenceNumber() {
		return sequenceNumber;
	}

	public InetAddress getPeerAddress() {
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
	
	private Packet(PacketType packetType, long sequenceNumber, InetAddress peerAddress, int port, byte[] data) {
		super();
		this.packetType = packetType;
        this.sequenceNumber = sequenceNumber;
		this.peerAddress = peerAddress;
		this.port = port;
		this.data = data;
    }
	
    public static Packet makePacket(ByteBuffer byteBuffer) {//TODO change this logic once I merge it
        try {
            byteBuffer.flip();
            PacketType type = PacketType.values()[byteBuffer.get()];
            long sequenceNumber = byteBuffer.getInt();
            byte[] address = {byteBuffer.get(), byteBuffer.get(), byteBuffer.get(), byteBuffer.get()};
            int port = byteBuffer.getShort() + 65536;
            PacketBuilder builder = new PacketBuilder()
                    .setPacketType(type)
                    .setSequenceNumber(sequenceNumber)
                    .setPeerAddress(InetAddress.getByAddress(address))
                    .setPort(port);
            byte[] payload = new byte[byteBuffer.remaining()];
            byteBuffer.get(payload);
            builder.setData(payload);
            return builder.build();
        } catch(UnknownHostException e) {
            System.out.println(e.getMessage());
            System.exit(1);
            return null;
        }
	}
	
	public static class PacketBuilder{
		private PacketType packetType = PacketType.ACK;
		private long sequenceNumber = 1L;
		private InetAddress peerAddress = null;
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
		public PacketBuilder setPeerAddress(InetAddress peerAddress) {
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
        assert(peerAddress.getAddress().length == 4);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024).order(ByteOrder.BIG_ENDIAN);
        byteBuffer.clear();
        byteBuffer.put((byte) packetType.getValue());
		byteBuffer.putInt((int) sequenceNumber);
		byteBuffer.put(peerAddress.getAddress());
		byteBuffer.putShort((short)port);
		byteBuffer.put(data);
		byteBuffer.flip();
		return byteBuffer;
	}
}
