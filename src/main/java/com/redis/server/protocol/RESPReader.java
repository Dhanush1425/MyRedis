package com.redis.server.protocol;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RESPReader {

    private final Map<SocketChannel, StringBuilder> clientBuffers = new ConcurrentHashMap<>();

    public void append(SocketChannel client, String data) {
        clientBuffers.computeIfAbsent(client,
                        c -> new StringBuilder()).append(data);
    }

    public void remove(SocketChannel client) {
        clientBuffers.remove(client);
    }

    public String nextRequest(SocketChannel client) {
        StringBuilder buffer = clientBuffers.get(client);
        if (buffer == null || buffer.length() == 0) {
            return null;
        }
        // Find first line
        int firstCRLF = findCRLF(buffer, 0);//2
        if (firstCRLF == -1) {
            return null;
        }
        String firstLine = buffer.substring(0, firstCRLF);//*3

        if (!firstLine.startsWith("*")) {
            throw new IllegalArgumentException("Invalid RESP Array");
        }

        int elementCount = Integer.parseInt(firstLine.substring(1));//3
        int cursor = firstCRLF + 2;//skip /r/n  // 4

        // Walk through every bulk string
        for (int i = 0; i < elementCount; i++) {
            int lengthCRLF = findCRLF(buffer, cursor);//6
            if (lengthCRLF == -1) {
                return null;
            }

            String lengthLine = buffer.substring(cursor, lengthCRLF);//(4, 6)
            if (!lengthLine.startsWith("$")) {
                throw new IllegalArgumentException("Invalid Bulk String");
            }

            int length = Integer.parseInt(lengthLine.substring(1));//4
            cursor = lengthCRLF + 2;
            // Not enough bytes for data + CRLF
            if (cursor + length + 2 > buffer.length()) {
                return null;
            }
            cursor += length + 2;
        }
        String request = buffer.substring(0, cursor);
        buffer.delete(0, cursor);
        return request;
    }

    private int findCRLF(StringBuilder buffer, int start) {
        for (int i = start; i < buffer.length() - 1; i++) {
            if (buffer.charAt(i) == '\r' && buffer.charAt(i + 1) == '\n') {
                return i;
            }
        }
        return -1;
    }

}