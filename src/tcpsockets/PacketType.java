package tcpsockets;

public enum PacketType{
	ACK(0), SYN(1), SYNACK(2);

	private int value;
	
	private PacketType(int value){
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
}
