package com.redis.server.executer;

import com.redis.server.command.Command;
//import com.redis.commands.HGetCommand;
import com.redis.server.commands.*;
import com.redis.server.persistence.AppendOnlyFile;
import com.redis.server.replication.ReplicaManager;
import com.redis.server.response.ErrorResponse;
import com.redis.server.response.Response;
import com.redis.server.storage.MemoryDatabase;

import java.util.HashMap;
import java.util.Map;

public class CommandExecuter {

    private final Map<String, CommandHandler> handlers =
            new HashMap<>();

    public CommandExecuter(MemoryDatabase database, AppendOnlyFile aof, ReplicaManager replicaManager) {

        handlers.put("PING", new PingCommand());

        handlers.put("SET", new SetCommand(database, aof, replicaManager));

        handlers.put("GET", new GetCommand(database));

        handlers.put("DEL", new DeleteCommand(database, aof, replicaManager));

        handlers.put("EXPIRE", new ExpireCommand(database, aof, replicaManager));

        handlers.put("TTL", new TTLCommand(database));

        handlers.put("HSET", new HSetCommand(database, aof, replicaManager));

        handlers.put("HGET", new HGetCommand(database));

        handlers.put("LPUSH", new LPushCommand(database, aof, replicaManager));

        handlers.put("LPOP", new LPopCommand(database, aof, replicaManager));

        handlers.put("RPUSH", new RPushCommand(database, aof, replicaManager));

        handlers.put("RPOP", new RPopCommand(database, aof, replicaManager));

        handlers.put("LRANGE", new LRangeCommand(database));

        handlers.put("SETNX", new SETNXCommand(database));

        handlers.put("EXISTS", new EXISTSCommand(database));

        handlers.put("INCR", new INCRCommand(database));

        handlers.put("DECR", new DECRCommand(database));
    }

    public Response execute(Command command) {

        CommandHandler handler = handlers.get(command.commandName().toUpperCase());

        if (handler == null) {
            return new ErrorResponse("Unknown Command");
        }

        try {
            return handler.execute(command.arguments());
        }catch (IllegalArgumentException e) {
            return new ErrorResponse(e.getMessage());
        }
    }
}