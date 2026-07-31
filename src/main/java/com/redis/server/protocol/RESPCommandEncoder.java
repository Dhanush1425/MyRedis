package com.redis.server.protocol;

import java.util.List;

public class RESPCommandEncoder {

    public String encode(String command, List<String> arguments) {

        StringBuilder builder = new StringBuilder();
        int totalElements = arguments.size() + 1;

        builder.append("*")
                .append(totalElements)
                .append("\r\n");

        // Command
        builder.append("$")
                .append(command.length())
                .append("\r\n");

        builder.append(command)
                .append("\r\n");

        // Arguments
        for (String arg : arguments) {
            builder.append("$")
                    .append(arg.length())
                    .append("\r\n");

            builder.append(arg)
                    .append("\r\n");
        }
        return builder.toString();
    }
}