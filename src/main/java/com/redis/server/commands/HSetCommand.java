package com.redis.server.commands;

import com.redis.server.persistence.AppendOnlyFile;
import com.redis.server.persistence.RecoveryContext;
import com.redis.server.protocol.RESPCommandEncoder;

import com.redis.server.replication.ReplicaManager;
import com.redis.server.replication.ReplicationContext;
import com.redis.server.response.ErrorResponse;
import com.redis.server.response.IntegerResponse;
import com.redis.server.response.Response;
import com.redis.server.storage.MemoryDatabase;

import java.util.List;

public class HSetCommand implements CommandHandler {

    private final MemoryDatabase database;
    private final AppendOnlyFile aof;
    private final ReplicaManager replicaManager;


    private final RESPCommandEncoder encoder = new RESPCommandEncoder();

    public HSetCommand(MemoryDatabase database,
                       AppendOnlyFile aof, ReplicaManager replicaManager) {
        this.database = database;
        this.aof = aof;
        this.replicaManager = replicaManager;
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
                if (!ReplicationContext.isReplicating()) {
                    replicaManager.broadcast(resp);
                }            }

            return new IntegerResponse(result);

        } catch (IllegalArgumentException e) {
            return new ErrorResponse(e.getMessage());
        }
    }
}