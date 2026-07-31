package com.redis.server.commands;

import com.redis.server.response.ErrorResponse;
import com.redis.server.response.IntegerResponse;
import com.redis.server.response.Response;
import com.redis.server.storage.MemoryDatabase;

import java.util.List;

public class EXISTSCommand implements CommandHandler {

    private final MemoryDatabase database;

    public EXISTSCommand(MemoryDatabase database) {
        this.database = database;
    }

    @Override
    public Response execute(List<String> arguments) {

        if (arguments.size() != 1) {
            return new ErrorResponse("EXISTS requires key");
        }

        boolean exists = database.exists(arguments.get(0));

        return new IntegerResponse(exists ? 1 : 0);
    }
}