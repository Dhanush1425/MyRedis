package com.redis.commands;

import com.redis.response.Response;

import java.util.List;

public interface CommandHandler {
    Response execute(List<String > arguments);
}
