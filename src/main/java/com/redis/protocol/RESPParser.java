package com.redis.protocol;

import com.redis.command.Command;

import java.util.ArrayList;
import java.util.List;

public class RESPParser {

    public Command parse(String  request){
        // Split request into lines
        String[] lines = request.split("\\r\\n");

        // Validate RESP Array
        if (lines.length == 0 || !lines[0].startsWith("*")) {
            throw new IllegalArgumentException("Invalid RESP Request");
        }

        int elementCount = Integer.parseInt(lines[0].substring(1));   // Number of elements
        List<String> values = new ArrayList<>();
        int index = 1;
        for (int i = 0; i < elementCount; i++) {
            // Validate Bulk String
            if (!lines[index].startsWith("$")) {
                throw new IllegalArgumentException("Invalid Bulk String");
            }
            index++; // Skip the length line
            values.add(lines[index]);  // Read actual value
            index++;            // Move to next bulk string
        }

        String commandName = values.get(0).toUpperCase();
        List<String> arguments =
                values.subList(1, values.size());

        return new Command(commandName, arguments);
    }
}
