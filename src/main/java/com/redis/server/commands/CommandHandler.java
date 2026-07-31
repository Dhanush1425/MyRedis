package com.redis.server.commands;

import com.redis.server.response.Response;

import java.util.List;

public interface CommandHandler {
    Response execute(List<String > arguments);
}
