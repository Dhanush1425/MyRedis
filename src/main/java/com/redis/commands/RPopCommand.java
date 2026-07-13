package com.redis.commands;

import com.redis.persistence.AppendOnlyFile;
import com.redis.persistence.RecoveryContext;
import com.redis.protocol.RESPCommandEncoder;
import com.redis.protocol.RESPEncoder;
import com.redis.replication.ReplicaManager;
import com.redis.replication.ReplicationContext;
import com.redis.response.BulkStringResponse;
import com.redis.response.ErrorResponse;
import com.redis.response.Response;
import com.redis.storage.MemoryDatabase;

import java.util.List;

public class RPopCommand implements CommandHandler {

    private final MemoryDatabase database;
    private final AppendOnlyFile aof;
    private final ReplicaManager replicaManager;

    private final RESPCommandEncoder encoder = new RESPCommandEncoder();

    public RPopCommand(MemoryDatabase database, AppendOnlyFile aof, ReplicaManager replicaManager) {
        this.database = database;
        this.aof = aof;
        this.replicaManager = replicaManager;
    }

    @Override
    public Response execute(List<String> args) {

        if (args.size() != 1) {
            return new ErrorResponse("RPOP requires key");
        }

        String key = args.get(0);
        String value = database.rpop(key);

        if (!RecoveryContext.isRecovering()) {
            String resp = encoder.encode("RPOP", List.of(key));
            aof.append(resp);
            if (!ReplicationContext.isReplicating()) {
                replicaManager.broadcast(resp);
            }
        }
        return new BulkStringResponse(value);
    }
}