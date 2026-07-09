package com.redis.commands;

import com.redis.response.BulkStringResponse;
import com.redis.response.ErrorResponse;
import com.redis.response.Response;
import com.redis.storage.MemoryDatabase;

import java.util.List;

public class HGetCommand implements CommandHandler {

    private final MemoryDatabase database;

    public HGetCommand(MemoryDatabase database) {
        this.database = database;
    }

    @Override
    public Response execute(List<String> args) {

        if (args.size() != 2) {
            return new ErrorResponse("HGET requires key and field");
        }

        String key = args.get(0);
        String field = args.get(1);

        try {
            String value = database.hget(key, field);
            return new BulkStringResponse(value);

        } catch (IllegalArgumentException e) {
            return new ErrorResponse(e.getMessage());
        }
    }
}