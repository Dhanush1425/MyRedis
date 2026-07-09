package com.redis.commands;

import com.redis.persistence.AppendOnlyFile;
import com.redis.persistence.RecoveryContext;
import com.redis.protocol.RESPCommandEncoder;
import com.redis.protocol.RESPEncoder;
import com.redis.response.ErrorResponse;
import com.redis.response.IntegerResponse;
import com.redis.response.Response;
import com.redis.storage.MemoryDatabase;

import java.util.List;

public class DeleteCommand implements CommandHandler {

    private final MemoryDatabase database;
    private final AppendOnlyFile aof;
    private final RESPCommandEncoder encoder = new RESPCommandEncoder();


    public DeleteCommand(MemoryDatabase database, AppendOnlyFile aof) {
        this.database = database;
        this.aof = aof;
    }

    @Override
    public Response execute(List<String> args) {

        if (args.size() != 1) {
            return new ErrorResponse("DEL requires key");        }

        String key = args.get(0);
        boolean deleted = database.delete(key);

        if (deleted && !RecoveryContext.isRecovering()) {
            String resp = encoder.encode("DEL", List.of(key));
            aof.append(resp);
        }
        return new IntegerResponse(deleted ? 1 : 0);
    }
}