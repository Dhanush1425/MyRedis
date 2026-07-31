package com.redis.server.commands;

import com.redis.server.response.ArrayResponse;
import com.redis.server.response.ErrorResponse;
import com.redis.server.response.Response;
import com.redis.server.storage.MemoryDatabase;
import java.util.List;

public class LRangeCommand implements CommandHandler {

    private final MemoryDatabase database;

    public LRangeCommand(MemoryDatabase database) {
        this.database = database;
    }

    @Override
    public Response execute(List<String> args) {

        if (args.size() != 3) {
            return new ErrorResponse("LRANGE requires key start stop");
        }

        String key = args.get(0);

        int start = Integer.parseInt(args.get(1));
        int end = Integer.parseInt(args.get(2));

        List<String> values = database.lrange(key, start, end);

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            result.append(i + 1)
                    .append(") ")
                    .append(values.get(i))
                    .append(" ");
        }

        return new ArrayResponse(values);
    }
}