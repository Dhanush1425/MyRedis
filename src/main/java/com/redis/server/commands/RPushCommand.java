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

public class RPushCommand implements CommandHandler {

    private final MemoryDatabase database;
    private final AppendOnlyFile aof;
    private final ReplicaManager replicaManager;

    private final RESPCommandEncoder encoder = new RESPCommandEncoder();

    public RPushCommand(MemoryDatabase database, AppendOnlyFile aof, ReplicaManager replicaManager) {
        this.database = database;
        this.aof = aof;
        this.replicaManager = replicaManager;
    }

    @Override
    public Response execute(List<String> args) {

        if (args.size() != 2) {
            return new ErrorResponse("RPUSH requires key and value");
        }

        String key = args.get(0);
        String value = args.get(1);

        int size = database.rpush(key, value);

        if (!RecoveryContext.isRecovering()) {
            String resp = encoder.encode("RPUSH", List.of(key, value));
            aof.append(resp);
            if (!ReplicationContext.isReplicating()) {
                replicaManager.broadcast(resp);
            }
        }

        return new IntegerResponse(size);
    }
}