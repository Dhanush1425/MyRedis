package com.redis.commands;

import com.redis.persistence.AppendOnlyFile;
import com.redis.persistence.RecoveryContext;
import com.redis.protocol.RESPCommandEncoder;
import com.redis.storage.MemoryDatabase;

import java.util.List;

public class ExpireCommand implements CommandHandler {

    private final MemoryDatabase database;
    private final AppendOnlyFile aof;

    private final RESPCommandEncoder encoder =
            new RESPCommandEncoder();

    public ExpireCommand(MemoryDatabase database, AppendOnlyFile aof) {

        this.database = database;

        this.aof = aof;
    }

    @Override
    public String execute(List<String> arguments) {

        if (arguments.size() != 2) {
            return "ERROR: EXPIRE requires key and seconds";
        }
        String key = arguments.get(0);
        long seconds;

        try {
            seconds = Long.parseLong(arguments.get(1));
        } catch (NumberFormatException e) {
            return "ERROR: Invalid expiry";
        }

//        aof.append("EXPIRE " + key + " " + seconds);
        String resp = encoder.encode("EXPIRE", List.of(key, String.valueOf(seconds)));

        if (!RecoveryContext.isRecovering()) {
            aof.append(resp);
        }

        boolean success = database.expire(key, seconds);

        return success ? "1" : "0";

    }

}