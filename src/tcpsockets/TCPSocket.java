package tcpsockets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import tcpsockets.Packet.PacketBuilder;

public abstract class TCPSocket implements Runnable {
    protected DatagramChannel channel;

    protected InetSocketAddress targetAddress;
    protected InetSocketAddress routerAddress;

    private PipedInputStream input;
    private PipedOutputStream output;

    /**
     * Creates a new TCP socket that uses a datagram channel to send datagrams to
     * the target address
     * 
     * @param target The address to which datagrams will be sent
     */
    public TCPSocket(InetSocketAddress targetAddress, InetSocketAddress routerAddress) {
        try {
            channel = DatagramChannel.open();
            this.targetAddress = targetAddress;
            this.routerAddress = routerAddress;
            input = new PipedInputStream();
            output = new PipedOutputStream();
            new Thread(this).start();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        setupChannel();
        try {
            selectiveRepeat();
        } catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public abstract void setupChannel();

    public OutputStream getOutputStream() throws IOException {
        return new PipedOutputStream(input);
    }

    public InputStream getInputStream() throws IOException {
        return new PipedInputStream(output);
    }

    private void selectiveRepeat() throws IOException {
        long currentSequenceNumber = 2;
        long oldestUnackedPacket = currentSequenceNumber;

        long receiveBase = 2;
        int windowSize = 3;
        int timeout = 1000;

        byte[] data = new byte[Packet.MAX_PAYLOAD_SIZE];
        int pointer = 0;

        HashMap<Long, PacketStatusPair> packets = new HashMap<Long, PacketStatusPair>();

        Selector selector = Selector.open();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);
        while (true) {
            // make new packets from the input
            while (input.available() > 0) {
                data[pointer] = (byte) input.read();
                pointer++;
                if (pointer >= Packet.MAX_PAYLOAD_SIZE) {
                    // Create a new packet to send
                    Packet packet = new PacketBuilder().setPacketType(PacketType.DATA)
                            .setPeerAddress(targetAddress.getAddress()).setPort(targetAddress.getPort()).setData(data)
                            .setSequenceNumber(currentSequenceNumber).build();

                    // Add to the unsent packets
                    packets.put(currentSequenceNumber, new PacketStatusPair(packet));

                    // Increment the sequence number
                    currentSequenceNumber += 1;

                    // Reset the data
                    pointer = 0;
                }
            }

            // send packets
            for (long i = oldestUnackedPacket; i < oldestUnackedPacket + windowSize; i++) {
                PacketStatusPair packetPair = packets.get(i);
                if (packetPair.status == PacketStatusPair.Status.NOT_SENT) {
                    channel.write(packetPair.packet.toByteBuffer());
                    packetPair.status = PacketStatusPair.Status.NACK;
                }
            }

            // timeout
            selector.select(1000);
            Set<SelectionKey> keys = selector.selectedKeys();

            boolean receivedAck = false;
            if (keys.contains(SelectionKey.OP_READ)) {
                Packet receivedPacket = receivePacket();
                if (receivedPacket.getPacketType() == PacketType.ACK) {
                    PacketStatusPair ackedPacket = packets.get(receivedPacket.getSequenceNumber());
                    ackedPacket.status = PacketStatusPair.Status.ACK;
                    receivedAck = true;
                    // increment base
                    long newSendBase = oldestUnackedPacket;
                    while (packets.get(newSendBase).status == PacketStatusPair.Status.ACK) {
                        newSendBase++;
                    }
                    oldestUnackedPacket = newSendBase;
                } else if (receivedPacket.getPacketType() == PacketType.DATA) {
                    // receive data
                }
            }

            // resend nack packets
            if (!receivedAck) {
                for (long i = oldestUnackedPacket; i < oldestUnackedPacket + windowSize; i++) {
                    PacketStatusPair packetPair = packets.get(i);
                    if (packetPair.status == PacketStatusPair.Status.NACK) {
                        channel.write(packetPair.packet.toByteBuffer());
                    }
                }
            }
        }
    }

    private Packet receivePacket() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Packet.MAX_PACKET_SIZE);
        try {
            channel.receive(byteBuffer);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Packet.makePacket(byteBuffer);
    }
}
