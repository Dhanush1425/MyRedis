package com.redis.commands;

import com.redis.persistence.AppendOnlyFile;
import com.redis.persistence.RecoveryContext;
import com.redis.protocol.RESPCommandEncoder;
import com.redis.replication.ReplicaManager;
import com.redis.replication.ReplicationContext;
import com.redis.response.ErrorResponse;
import com.redis.response.IntegerResponse;
import com.redis.response.Response;
import com.redis.storage.MemoryDatabase;

import java.util.List;

public class ExpireCommand implements CommandHandler {

    private final MemoryDatabase database;
    private final AppendOnlyFile aof;
    private final ReplicaManager replicaManager;


    private final RESPCommandEncoder encoder =
            new RESPCommandEncoder();

    public ExpireCommand(MemoryDatabase database, AppendOnlyFile aof, ReplicaManager replicaManager) {

        this.database = database;

        this.aof = aof;
        this.replicaManager = replicaManager;
    }

    @Override
    public Response execute(List<String> arguments) {

        if (arguments.size() != 2) {
            return new ErrorResponse("EXPIRE requires key and seconds");
        }
        String key = arguments.get(0);
        long seconds;

        try {
            seconds = Long.parseLong(arguments.get(1));
        } catch (NumberFormatException e) {
            return new ErrorResponse("Invalid Expiry");
        }

//        aof.append("EXPIRE " + key + " " + seconds);
        String resp = encoder.encode("EXPIRE", List.of(key, String.valueOf(seconds)));

        if (!RecoveryContext.isRecovering()) {
            aof.append(resp);
            if (!ReplicationContext.isReplicating()) {
                replicaManager.broadcast(resp);
            }        }

        boolean success = database.expire(key, seconds);

        return new IntegerResponse(success ? 1 : 0);
    }

}