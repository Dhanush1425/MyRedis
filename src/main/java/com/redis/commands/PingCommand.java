package com.redis.commands;

import com.redis.response.Response;
import com.redis.response.SimpleStringResponse;

import java.util.List;

public class PingCommand implements CommandHandler{

    @Override
    public Response execute(List<String> arguments) {
        return new SimpleStringResponse("PONG");
    }
}
