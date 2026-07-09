package com.redis.commands;

import com.redis.response.BulkStringResponse;
import com.redis.response.ErrorResponse;
import com.redis.response.Response;
import com.redis.storage.MemoryDatabase;

import java.util.List;

public class GetCommand implements CommandHandler {

    private final MemoryDatabase database;

    public GetCommand(MemoryDatabase database) {

        this.database = database;

    }

    @Override
    public Response execute(List<String> arguments) {

        if (arguments.size() != 1) {
            return new ErrorResponse("GET requires key");
        }

        String value = database.get(arguments.get(0));
        return new BulkStringResponse(value);

    }

}