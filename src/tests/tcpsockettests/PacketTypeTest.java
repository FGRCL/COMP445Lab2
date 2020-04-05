package tests.tcpsockettests;

import org.junit.Test;

import tcpsockets.PacketType;

public class PacketTypeTest {
	@Test
	public void PacketTypeToString() {
		assert(PacketType.ACK.toString().equals("ACK"));
	}
}
