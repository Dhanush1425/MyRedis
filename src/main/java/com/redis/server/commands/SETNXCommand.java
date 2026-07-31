package com.redis.server.commands;

import com.redis.server.response.ErrorResponse;
import com.redis.server.response.IntegerResponse;
import com.redis.server.response.Response;
import com.redis.server.storage.MemoryDatabase;

import java.util.List;

public class SETNXCommand implements CommandHandler {

    private final MemoryDatabase database;

    public SETNXCommand(MemoryDatabase database) {
        this.database = database;
    }

    @Override
    public Response execute(List<String> arguments) {

        if (arguments.size() != 2) {
            return new ErrorResponse("SETNX requires key and value");
        }

        String key = arguments.get(0);
        String value = arguments.get(1);

        boolean success = database.setIfAbsent(key, value);

        return new IntegerResponse(success ? 1 : 0);
    }
}