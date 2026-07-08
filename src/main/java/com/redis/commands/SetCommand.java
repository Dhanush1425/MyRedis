package com.redis.commands;

import com.redis.persistence.AppendOnlyFile;
import com.redis.persistence.RecoveryContext;
import com.redis.protocol.RESPCommandEncoder;
import com.redis.storage.MemoryDatabase;

import java.util.List;

public class SetCommand implements CommandHandler {

    private final MemoryDatabase database;
    private final AppendOnlyFile aof;

    private final RESPCommandEncoder encoder =
            new RESPCommandEncoder();

    public SetCommand(MemoryDatabase database, AppendOnlyFile aof) {
        this.database = database;
        this.aof = aof;
    }

    @Override
    public String execute(List<String> arguments) {

        if (arguments.size() != 2) {
            return "ERROR: SET requires key and value";
        }

        String key = arguments.get(0);
        String value = arguments.get(1);

//        aof.append("SET "+key+" "+value);
        String resp = encoder.encode("SET", List.of(key, value)); // convert into RESP
        if (!RecoveryContext.isRecovering()) {
            aof.append(resp);
        }
        database.set(key, value);

//        database.set(arguments.get(0), arguments.get(1));
        return "OK";
    }

}