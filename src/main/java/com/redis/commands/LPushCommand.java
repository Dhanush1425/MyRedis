package com.redis.commands;

import com.redis.persistence.AppendOnlyFile;
import com.redis.persistence.RecoveryContext;
import com.redis.protocol.RESPCommandEncoder;
import com.redis.protocol.RESPEncoder;
import com.redis.replication.ReplicaManager;
import com.redis.replication.ReplicationContext;
import com.redis.response.ErrorResponse;
import com.redis.response.IntegerResponse;
import com.redis.response.Response;
import com.redis.storage.MemoryDatabase;

import java.util.List;

public class LPushCommand implements CommandHandler {

    private final MemoryDatabase database;
    private final AppendOnlyFile aof;
    private final ReplicaManager replicaManager;

    private final RESPCommandEncoder encoder = new RESPCommandEncoder();

    public LPushCommand(MemoryDatabase database,
                        AppendOnlyFile aof, ReplicaManager replicaManager) {

        this.database = database;
        this.aof = aof;
        this.replicaManager = replicaManager;
    }

    @Override
    public Response execute(List<String> args) {

        if (args.size() != 2) {
            return new ErrorResponse("LPUSH requires key and value");        }

        String key = args.get(0);
        String value = args.get(1);

        int size = database.lpush(key, value);

        if (!RecoveryContext.isRecovering()) {
            String resp = encoder.encode("LPUSH", List.of(key, value));
            aof.append(resp);
            if (!ReplicationContext.isReplicating()) {
                replicaManager.broadcast(resp);
            }
        }

        return new IntegerResponse(size);
    }
}