package com.redis.server.commands;

import com.redis.server.response.BulkStringResponse;
import com.redis.server.response.ErrorResponse;
import com.redis.server.response.Response;
import com.redis.server.storage.MemoryDatabase;

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