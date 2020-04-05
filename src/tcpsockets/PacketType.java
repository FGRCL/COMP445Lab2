package tcpsockets;

public enum PacketType{
	ACK(0), SYN(1), SYNACK(2), DATA(3), FIN(4);

	private int value;
	
	private PacketType(int value){
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
}
