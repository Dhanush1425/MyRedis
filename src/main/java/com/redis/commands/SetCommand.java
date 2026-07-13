package com.redis.commands;

import com.redis.persistence.AppendOnlyFile;
import com.redis.persistence.RecoveryContext;
import com.redis.protocol.RESPCommandEncoder;
import com.redis.replication.ReplicaManager;
import com.redis.replication.ReplicationContext;
import com.redis.response.ErrorResponse;
import com.redis.response.Response;
import com.redis.response.SimpleStringResponse;
import com.redis.storage.MemoryDatabase;

import java.util.List;

public class SetCommand implements CommandHandler {

    private final MemoryDatabase database;
    private final AppendOnlyFile aof;
    private final ReplicaManager replicaManager;

    private final RESPCommandEncoder encoder =
            new RESPCommandEncoder();

    public SetCommand(MemoryDatabase database, AppendOnlyFile aof, ReplicaManager replicaManager) {
        this.database = database;
        this.aof = aof;
        this.replicaManager = replicaManager;
    }

    @Override
    public Response execute(List<String> arguments) {

        if (arguments.size() != 2) {
            return new ErrorResponse("SET requires key and value");
        }

        String key = arguments.get(0);
        String value = arguments.get(1);

//        aof.append("SET "+key+" "+value);
        String resp = encoder.encode("SET", List.of(key, value)); // convert into RESP
        if (!RecoveryContext.isRecovering()) {
            aof.append(resp);
            System.out.println("fifnised aof");
            if (!ReplicationContext.isReplicating()) {
                replicaManager.broadcast(resp);
            }
            System.out.println("replicamanagerr");
        }
        database.set(key, value);

//        database.set(arguments.get(0), arguments.get(1));
        return new SimpleStringResponse("OK");
    }

}