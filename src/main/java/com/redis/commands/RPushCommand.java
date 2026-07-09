package com.redis.commands;

import com.redis.persistence.AppendOnlyFile;
import com.redis.persistence.RecoveryContext;
import com.redis.protocol.RESPCommandEncoder;
import com.redis.protocol.RESPEncoder;
import com.redis.storage.MemoryDatabase;

import java.util.List;

public class RPushCommand implements CommandHandler {

    private final MemoryDatabase database;
    private final AppendOnlyFile aof;
    private final RESPCommandEncoder encoder = new RESPCommandEncoder();

    public RPushCommand(MemoryDatabase database, AppendOnlyFile aof) {
        this.database = database;
        this.aof = aof;
    }

    @Override
    public String execute(List<String> args) {

        if (args.size() != 2) {
            return "ERROR: RPUSH requires key and value";
        }

        String key = args.get(0);
        String value = args.get(1);

        int size = database.rpush(key, value);

        if (!RecoveryContext.isRecovering()) {
            String resp = encoder.encode("RPUSH", List.of(key, value));
            aof.append(resp);
        }

        return String.valueOf(size);
    }
}