package com.redis.commands;

import com.redis.persistence.AppendOnlyFile;
import com.redis.persistence.RecoveryContext;
import com.redis.protocol.RESPCommandEncoder;

import com.redis.response.ErrorResponse;
import com.redis.response.IntegerResponse;
import com.redis.response.Response;
import com.redis.storage.MemoryDatabase;

import java.util.List;

public class HSetCommand implements CommandHandler {

    private final MemoryDatabase database;
    private final AppendOnlyFile aof;
    private final RESPCommandEncoder encoder = new RESPCommandEncoder();

    public HSetCommand(MemoryDatabase database,
                       AppendOnlyFile aof) {
        this.database = database;
        this.aof = aof;
    }

    @Override
    public Response execute(List<String> args) {

        if (args.size() != 3) {
            return new ErrorResponse("HSET requires key field value");
        }

        String key = args.get(0);
        String field = args.get(1);
        String value = args.get(2);

        try {

            int result = database.hset(key, field, value);// in memory
            String resp = encoder.encode("HSET", List.of(key, field, value)); // covert to RESP

            // Append only if this is not recovery
            if (!RecoveryContext.isRecovering()) {
                aof.append(resp);
            }

            return new IntegerResponse(result);

        } catch (IllegalArgumentException e) {
            return new ErrorResponse(e.getMessage());
        }
    }
}