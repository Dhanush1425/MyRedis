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

public class DeleteCommand implements CommandHandler {

    private final MemoryDatabase database;
    private final AppendOnlyFile aof;
    private final ReplicaManager replicaManager;


    private final RESPCommandEncoder encoder = new RESPCommandEncoder();


    public DeleteCommand(MemoryDatabase database, AppendOnlyFile aof, ReplicaManager replicaManager) {
        this.database = database;
        this.aof = aof;
        this.replicaManager = replicaManager;
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
            if (!ReplicationContext.isReplicating()) {
                replicaManager.broadcast(resp);
            }        }
        return new IntegerResponse(deleted ? 1 : 0);
    }
}