package com.redis.commands;

import java.util.List;

public class PingCommand implements CommandHandler{

    @Override
    public String execute(List<String> arguments) {
        return "PONG";
    }
}
