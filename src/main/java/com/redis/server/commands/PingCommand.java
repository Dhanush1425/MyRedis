package com.redis.server.commands;

import com.redis.server.response.Response;
import com.redis.server.response.SimpleStringResponse;

import java.util.List;

public class PingCommand implements CommandHandler{

    @Override
    public Response execute(List<String> arguments) {
        return new SimpleStringResponse("PONG");
    }
}
