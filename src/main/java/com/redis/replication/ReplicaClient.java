package com.redis.replication;

import com.redis.command.Command;
import com.redis.executer.CommandExecuter;
import com.redis.protocol.RESPParser;
import com.redis.protocol.RESPReader;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class ReplicaClient {

    private final String host;
    private final int port;

    private final RESPReader reader = new RESPReader();
    private final RESPParser parser = new RESPParser();
    private final CommandExecuter executer;

    private SocketChannel masterChannel;

    public ReplicaClient(String host,
                         int port,
                         CommandExecuter executer) {

        this.host = host;
        this.port = port;
        this.executer = executer;
    }

    public void connect() throws IOException {

        masterChannel = SocketChannel.open();
        masterChannel.connect(new InetSocketAddress(host, port));

        String handshake = "*1\r\n" +
                        "$7\r\n" +
                        "REPLICA\r\n";

        masterChannel.write(ByteBuffer.wrap(handshake.getBytes(StandardCharsets.UTF_8)));

        System.out.println("Connected to master at " + host + ":" + port);

        // Consume master's handshake reply (+OK)
        ByteBuffer ackBuffer = ByteBuffer.allocate(128);

        int bytesRead = masterChannel.read(ackBuffer);
        if (bytesRead > 0) {
            ackBuffer.flip();
            String ack = StandardCharsets.UTF_8.decode(ackBuffer).toString();
            System.out.println("Handshake response:");
            System.out.println(ack.trim());
        }

        listen();
    }

    public SocketChannel getMasterChannel() {
        return masterChannel;
    }

    public boolean isConnected() {
        return masterChannel != null &&
                masterChannel.isConnected();
    }

    public void disconnect() throws IOException {
        if (masterChannel != null) {
            masterChannel.close();
        }
    }

    private void listen() throws IOException {

        ByteBuffer buffer = ByteBuffer.allocate(4096);

        while (true) {

            int bytesRead = masterChannel.read(buffer);
            if (bytesRead == -1) {
                System.out.println("Master disconnected.");
                masterChannel.close();
                break;
            }

            if (bytesRead == 0) {
                continue;
            }

            buffer.flip();
            String incoming = StandardCharsets.UTF_8.decode(buffer).toString();
            buffer.clear();

            System.out.println("RAW FROM MASTER:");
            System.out.println(incoming);

            // Ignore simple-string replies such as +OK
            if (incoming.startsWith("+")) {
                System.out.println("Ignoring reply: "
                        + incoming.trim());
                continue;
            }

            reader.append(masterChannel, incoming);

            String request;

            while ((request = reader.nextRequest(masterChannel)) != null) {
                System.out.println("Received from master:");
                System.out.println(request);

                Command command = parser.parse(request);
                System.out.println("Executing: " + command);

                ReplicationContext.begin();
                try {
                    executer.execute(command);
                } finally {
                    ReplicationContext.end();
                }
            }
        }
    }
}