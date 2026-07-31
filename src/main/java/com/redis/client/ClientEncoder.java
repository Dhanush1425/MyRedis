package com.redis.client;

import java.util.List;

public final class ClientEncoder {

    private ClientEncoder() {
    }

    public static String encode(String command, List<String> arguments) {
        StringBuilder builder = new StringBuilder();
        int totalParts = 1 + arguments.size();
        builder.append("*").append(totalParts).append("\r\n");//*2/r/n
        appendBulkString(builder, command);
        for (String argument : arguments) {
            appendBulkString(builder, argument);//$3 GET ,$4 name
        }
        return builder.toString();
    }

    private static void appendBulkString(StringBuilder builder, String value) {
        builder.append("$").append(value.length()).append("\r\n");
        builder.append(value).append("\r\n");
    }
}