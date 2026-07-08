package com.redis.protocol;

import com.redis.command.Command;

import java.util.Arrays;
import java.util.List;

public class CommandParser {
    public Command parse(String input){
        input = input.trim();
        if (input.isBlank()){
            throw new IllegalArgumentException("Command cannot be empty");
        }
        String[] parts = input.split("\\s+");
        String commandName = parts[0].toUpperCase();
        List<String> arguments = Arrays.stream(parts).skip(1).toList();
        return new Command(commandName, arguments);
    }
}
