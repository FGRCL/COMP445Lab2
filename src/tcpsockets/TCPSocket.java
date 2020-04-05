package tcpsockets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.logging.Logger;

import tcpsockets.Packet.PacketBuilder;

public abstract class TCPSocket implements Runnable {
    Logger log = Logger.getLogger(TCPSocket.class.getName());
    
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

    private long currentSequenceNumber;
    private HashMap<Long, PacketStatusPair> packets;
    byte[] data;
    int pointer;
    boolean closed;

    private void selectiveRepeat() throws IOException {
        currentSequenceNumber = 2;
        long sendBase = currentSequenceNumber;
        long receiveBase = 2;
        int windowSize = 3;

        Stopwatch stopwatch = new Stopwatch();
        stopwatch.start();
        int timeout = 1000;

        data = new byte[Packet.MAX_PAYLOAD_SIZE];
        pointer = 0;

        packets = new HashMap<Long, PacketStatusPair>();

        HashMap<Long, Packet> incomingPackets = new HashMap<Long, Packet>();

        Selector selector = Selector.open();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);
        while (true) {
            // make new packets from the input
            while (input.available() > 0) {
                data[pointer] = (byte) input.read();
                pointer++;
                if (pointer >= Packet.MAX_PAYLOAD_SIZE) {
                    sendDataPacket();
                }
            }
            if(pointer > 0) {
                sendDataPacket();
            }

            // send packets
            for (long i = sendBase; i < sendBase + windowSize; i++) {
                PacketStatusPair packetPair = packets.get(i);
                if (packetPair != null && packetPair.status == PacketStatusPair.Status.NOT_SENT) {
                    channel.write(packetPair.packet.toByteBuffer());
                    packetPair.status = PacketStatusPair.Status.NACK;
                    stopwatch.reset();
                }
            }
            
            Packet receivedPacket = receivePacket();
            while(receivedPacket != null) {
                logPacketReceived(receivedPacket);
                if (receivedPacket.getPacketType() == PacketType.ACK) {
                    PacketStatusPair ackedPacket = packets.get(receivedPacket.getSequenceNumber());
                    if(ackedPacket == null) {
                        log.info("WARNING: Unexpected ACK with sequence number " + receivedPacket.getSequenceNumber());
                        break;
                    }
                    ackedPacket.status = PacketStatusPair.Status.ACK;

                    // increment base
                    long newSendBase = sendBase;
                    while (packets.get(newSendBase) != null && packets.get(newSendBase).status == PacketStatusPair.Status.ACK) {
                        newSendBase++;
                    }
                    sendBase = newSendBase;

                    // Check if we are closed and have sent everything
                    if(closed && sendBase == currentSequenceNumber && input.available() == 0) {
                        sendFinPacket();
                    }

                } else if (receivedPacket.getPacketType() == PacketType.DATA) {
                    // Add the received data to our buffer
                    if(receivedPacket.getSequenceNumber() >= receiveBase)
                        incomingPackets.put(receivedPacket.getSequenceNumber(), receivedPacket);

                    // Send an ACK back to the other end
                    sendAck(receivedPacket.getSequenceNumber());

                    // If we are able to send data to the upper layer, send some data
                    while(incomingPackets.containsKey(receiveBase)) {
                        byte[] payload = incomingPackets.get(receiveBase).getData();
                        output.write(payload);
                        output.flush();
                        incomingPackets.remove(receiveBase);
                        receiveBase += 1;
                    }
                } else if(receivedPacket.getPacketType() == PacketType.FIN) {
                    sendFinAckPacket();
                    output.close();
                    return;
                } else if(receivedPacket.getPacketType() == PacketType.FINACK) {
                    sendAck(receivedPacket.getSequenceNumber());
                    output.close();
                    return;
                }
                receivedPacket = receivePacket();
            }

            // resend nack packets
            long time = stopwatch.getTime();
            if (time > timeout) {
                for (long i = sendBase; i < sendBase + windowSize; i++) {
                    PacketStatusPair packetPair = packets.get(i);
                    if (packetPair != null && packetPair.status == PacketStatusPair.Status.NACK) {
                        channel.write(packetPair.packet.toByteBuffer());
                    }
                }
                stopwatch.reset();
            }
        }
    }

    private void sendDataPacket() {
        // Create a new packet to send
        byte[] payload = new byte[pointer];
        for(int i = 0; i < pointer; i++) {
            payload[i] = data[i];
        }
        Packet packet = new PacketBuilder().setPacketType(PacketType.DATA)
                .setPeerAddress(targetAddress.getAddress()).setPort(targetAddress.getPort()).setData(payload)
                .setSequenceNumber(currentSequenceNumber).build();

        // Add to the unsent packets
        packets.put(currentSequenceNumber, new PacketStatusPair(packet));

        // Increment the sequence number
        currentSequenceNumber += 1;

        // Reset the data
        pointer = 0;

        logPacketSent(packet);
    }

    private Packet receivePacket() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Packet.MAX_PACKET_SIZE);
        try {
            SocketAddress socket = channel.receive(byteBuffer);
            if(socket == null)
                return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Packet.makePacket(byteBuffer);
    }

    private void sendAck(long sequenceNumber) throws IOException {
        Packet ack = new PacketBuilder()
        .setPacketType(PacketType.ACK)
        .setPeerAddress(targetAddress.getAddress())
        .setPort(targetAddress.getPort())
        .setSequenceNumber(sequenceNumber)
        .build();
        channel.write(ack.toByteBuffer());
        logPacketSent(ack);
    }

    public void close() {
        closed = true;
    }

    public void sendFinPacket() throws IOException {
        Packet fin = new PacketBuilder()
        .setPacketType(PacketType.FIN)
        .setPeerAddress(targetAddress.getAddress())
        .setPort(targetAddress.getPort())
        .setSequenceNumber(currentSequenceNumber)
        .build();
        packets.put(currentSequenceNumber, new PacketStatusPair(fin));
        logPacketSent(fin);
    }

    public void sendFinAckPacket() throws IOException {
        Packet finack = new PacketBuilder()
            .setPacketType(PacketType.FINACK)
            .setPeerAddress(targetAddress.getAddress())
            .setPort(targetAddress.getPort())
            .setSequenceNumber(currentSequenceNumber)
            .build();
        channel.write(finack.toByteBuffer());
        logPacketSent(finack);
    }

    private void logPacketReceived(Packet p) {
        log.info("Received " + p.getPacketType().toString() + " with sequence number " + p.getSequenceNumber()
         + " from " + p.getPort());
    }

    private void logPacketSent(Packet p) {
        log.info("Sent " + p.getPacketType().toString() + " with sequence number " + p.getSequenceNumber() + " to " + p.getPort());
    }
}
