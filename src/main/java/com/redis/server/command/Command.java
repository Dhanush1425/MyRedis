package com.redis.server.command;

import java.util.List;

public record Command(
    String commandName,
    List<String> arguments
){
}
