package tcpsockets;

public class PacketStatusPair {
	public Packet packet;
	public Status status;
	
	public enum Status{
		NOT_SENT, NACK, ACK;
	}
	
	public PacketStatusPair(Packet packet) {
		this.packet = packet;
		this.status = Status.NOT_SENT;
	}
}
