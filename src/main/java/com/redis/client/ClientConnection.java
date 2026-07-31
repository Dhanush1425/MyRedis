package com.redis.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class ClientConnection {

    private final String host;
    private final int port;

    private SocketChannel channel;

    public ClientConnection(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws IOException {

        if (isConnected()) {
            return;
        }

        channel = SocketChannel.open();
        channel.connect(new InetSocketAddress(host, port));
    }

    public void close() throws IOException {

        if (channel != null) {
            channel.close();
        }
    }

    public boolean isConnected() {
        return channel != null && channel.isConnected();
    }


//     * Sends raw RESP data to the server.

    public void send(String request) throws IOException {

        ByteBuffer buffer = ByteBuffer.wrap(request.getBytes(StandardCharsets.UTF_8));// encode to bytes here and write to socket

        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
    }


    // *Reads one response from the server.

    public String receive() throws IOException {

        ByteBuffer buffer = ByteBuffer.allocate(4096);

        int bytesRead = channel.read(buffer);

        if (bytesRead == -1) {
            throw new IOException("Server closed the connection.");
        }

        buffer.flip();

        return StandardCharsets.UTF_8.decode(buffer).toString();
    }
}