package com.redis.server.commands;

import com.redis.server.response.ErrorResponse;
import com.redis.server.response.IntegerResponse;
import com.redis.server.response.Response;
import com.redis.server.storage.MemoryDatabase;

import java.util.List;

public class TTLCommand implements CommandHandler {

    private final MemoryDatabase database;

    public TTLCommand(MemoryDatabase database) {
        this.database = database;
    }

    @Override
    public Response execute(List<String> arguments) {

        if (arguments.size() != 1) {
            return new ErrorResponse("TTL requires key");
        }

        String key = arguments.get(0);

        long ttl = database.ttl(key);

        return new IntegerResponse((int) ttl);
    }
}