package com.redis.server.commands;

import com.redis.server.response.ErrorResponse;
import com.redis.server.response.IntegerResponse;
import com.redis.server.response.Response;
import com.redis.server.storage.MemoryDatabase;

import java.util.List;

public class DECRCommand implements CommandHandler {

    private final MemoryDatabase database;

    public DECRCommand(MemoryDatabase database) {
        this.database = database;
    }

    @Override
    public Response execute(List<String> arguments) {

        if (arguments.size() != 1) {
            return new ErrorResponse("DECR requires key");
        }

        long value = database.decrement(arguments.get(0));

        return new IntegerResponse((int) value);
    }
}