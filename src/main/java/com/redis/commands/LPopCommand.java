package com.redis.commands;

import com.redis.persistence.AppendOnlyFile;
import com.redis.persistence.RecoveryContext;
import com.redis.protocol.RESPCommandEncoder;
import com.redis.protocol.RESPEncoder;
import com.redis.storage.MemoryDatabase;

import java.util.List;

public class LPopCommand implements CommandHandler {

    private final MemoryDatabase database;
    private final AppendOnlyFile aof;
    private final RESPCommandEncoder encoder = new RESPCommandEncoder();

    public LPopCommand(MemoryDatabase database, AppendOnlyFile aof) {

        this.database = database;
        this.aof = aof;
    }

    @Override
    public String execute(List<String> args) {

        if (args.size() != 1) {
            return "ERROR: LPOP requires key";
        }

        String key = args.get(0);
        String value = database.lpop(key);

        if (value == null) {
            return "(nil)";
        }

        if (!RecoveryContext.isRecovering()) {
            String resp = encoder.encode("LPOP", List.of(key));
            aof.append(resp);
        }

        return value;
    }
}